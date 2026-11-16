package mrRebujito.MrRebujito.security;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
@SpringBootTest
@AutoConfigureMockMvc
class SecurityRoutesTest {
    @Autowired MockMvc mvc;
    @Test void rutasPublicasYPrivadas()throws Exception{
        mvc.perform(get("/caseta")).andExpect(status().isOk());
        mvc.perform(get("/ayuntamiento")).andExpect(status().isOk());
        mvc.perform(get("/caseta/socios")).andExpect(status().isUnauthorized());
        mvc.perform(post("/administrador").contentType("application/json").content("{}")).andExpect(status().isUnauthorized());
        mvc.perform(post("/caseta/socios/1")).andExpect(status().isUnauthorized());
        mvc.perform(put("/solicitud/aceptar/1")).andExpect(status().isUnauthorized());
    }
    @Test @WithMockUser(authorities="SOCIO")
    void socioNoApruebaLicencias()throws Exception{
        mvc.perform(put("/solicitud/aceptar/1")).andExpect(status().isForbidden());
        mvc.perform(post("/producto").contentType("application/json").content("{}")).andExpect(status().isForbidden());
    }
    @Test @WithMockUser(authorities="CASETA")
    void casetaNoRegistraAyuntamientos()throws Exception{
        mvc.perform(post("/ayuntamiento").contentType("application/json").content("{}")).andExpect(status().isForbidden());
    }
}
