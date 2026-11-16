package mrRebujito.MrRebujito.security;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import mrRebujito.MrRebujito.service.ActorService;

class JWTUtilsTest {
    private static final String SECRET=Base64.getEncoder().encodeToString(
        "0123456789abcdef0123456789abcdef".getBytes());
    @Test void tokenValidoYFirmaManipulada(){
        JWTUtils jwt=new JWTUtils(SECRET,3600000,mock(ActorService.class));
        var auth=new UsernamePasswordAuthenticationToken("caseta1",null,
            List.of(new SimpleGrantedAuthority("CASETA")));
        String token=jwt.generateToken(auth);
        assertTrue(jwt.validateToken(token));
        assertEquals("caseta1",jwt.getUsernameOfToken(token));
        assertFalse(jwt.validateToken(token.substring(0,token.length()-8)+"modificado"));
    }
    @Test void rechazaClaveDebil(){
        String weak=Base64.getEncoder().encodeToString("1234".getBytes());
        assertThrows(IllegalArgumentException.class,()->new JWTUtils(weak,3600000,mock(ActorService.class)));
    }
}
