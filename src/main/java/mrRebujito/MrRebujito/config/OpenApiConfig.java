package mrRebujito.MrRebujito.config;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
@Configuration
@OpenAPIDefinition(info=@Info(title="MrRebujito · API de feria",version="1.0.0",
    description="Casetas, socios, cartas de productos y licencias municipales"))
@SecurityScheme(name="bearerAuth",type=SecuritySchemeType.HTTP,scheme="bearer",bearerFormat="JWT")
public class OpenApiConfig {}
