package pl.sparkbit.commons;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@SuppressWarnings({"checkstyle:hideutilityclassconstructor", "WeakerAccess"})
public class Paths {

    public static final String BUILD_INFO = "/admin/buildInfo";
}
