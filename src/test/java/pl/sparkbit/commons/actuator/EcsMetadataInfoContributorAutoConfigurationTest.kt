package pl.sparkbit.commons.actuator

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.assertj.core.api.Assertions
import org.junit.Rule
import org.junit.Test
import org.springframework.boot.actuate.info.Info
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.assertj.AssertableApplicationContext
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.http.MediaType


internal class EcsMetadataInfoContributorAutoConfigurationTest {

    private val contextRunner = ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(EcsMetadataInfoContributorAutoConfiguration::class.java))

    @JvmField
    @Rule
    var wireMockRule = WireMockRule(8089)

    @Test
    fun testDefaultConfig() {
        contextRunner.run { context: AssertableApplicationContext? ->
            Assertions.assertThat(context).getBean(EcsMetadataInfoContributor::class.java)
                .matches { bean ->
                    val builder = Info.Builder()
                    bean.contribute(builder)
                    builder.build().details.isEmpty()
                }
        }
    }

    @Test
    fun testDisabled() {
        contextRunner
            .withPropertyValues("sparkbit.commons.ecs-metadata-info-contributor-enabled=false")
            .run { context: AssertableApplicationContext? ->
                Assertions.assertThat(context).doesNotHaveBean(EcsMetadataInfoContributor::class.java)
            }
    }

    @Test
    fun testEcsEnv() {
        val response = """{
    "DockerId": "43481a6ce4842eec8fe72fc28500c6b52edcc0917f105b83379f88cac1ff3946",
    "Name": "nginx-curl",
    "DockerName": "ecs-nginx-5-nginx-curl-ccccb9f49db0dfe0d901",
    "Image": "nrdlngr/nginx-curl",
    "ImageID": "sha256:2e00ae64383cfc865ba0a2ba37f61b50a120d2d9378559dcd458dc0de47bc165",
    "Labels": {
        "com.amazonaws.ecs.cluster": "default",
        "com.amazonaws.ecs.container-name": "nginx-curl",
        "com.amazonaws.ecs.task-arn": "arn:aws:ecs:us-east-2:012345678910:task/9781c248-0edd-4cdb-9a93-f63cb662a5d3",
        "com.amazonaws.ecs.task-definition-family": "nginx",
        "com.amazonaws.ecs.task-definition-version": "5"
    },
    "DesiredStatus": "RUNNING",
    "KnownStatus": "RUNNING",
    "Limits": {
        "CPU": 512,
        "Memory": 512
    },
    "CreatedAt": "2018-02-01T20:55:10.554941919Z",
    "StartedAt": "2018-02-01T20:55:11.064236631Z",
    "Type": "NORMAL",
    "Networks": [
        {
            "NetworkMode": "awsvpc",
            "IPv4Addresses": [
                "10.0.2.106"
            ]
        }
    ]
}"""
        stubFor(get(urlEqualTo("/ecsInfo"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(response)))
        val port = wireMockRule.port()
        contextRunner
            .withPropertyValues("ECS_CONTAINER_METADATA_URI=http://localhost:$port/ecsInfo")
            .run { context: AssertableApplicationContext? ->
                Assertions.assertThat(context).getBean(EcsMetadataInfoContributor::class.java)
                    .matches { bean ->
                        val builder = Info.Builder()
                        bean.contribute(builder)
                        builder.build().details.containsKey("ecs-metadata")
                    }
            }
    }
}