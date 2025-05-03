package io.github.arthoura.documentation;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
             title = "Parking API",
             version = "1.0.1",
             contact = @Contact(
                     name = "FAD Parking API",
                     url = "http://arthoura.github.io",
                     email = "fadparking@gmail.com"
             ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licences/LICENCE")

        )
)

public class ParkingApplication extends Application {
}
