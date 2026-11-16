package mrRebujito.MrRebujito.security;

import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import mrRebujito.MrRebujito.entity.Actor;
import mrRebujito.MrRebujito.service.ActorService;

@Component
public class JWTUtils {
    private final SecretKey key;
    private final long expirationMs;
    private final ActorService actors;
    public JWTUtils(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.expiration-ms:3600000}") long expirationMs,ActorService actors) {
        byte[] keyBytes=Decoders.BASE64.decode(secret);
        if(keyBytes.length<32) throw new IllegalArgumentException("JWT_SECRET requiere al menos 32 bytes en Base64");
        if(expirationMs<60000) throw new IllegalArgumentException("JWT_EXPIRATION_MS debe ser >= 60000");
        this.key=Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs=expirationMs;
        this.actors=actors;
    }
    public String getToken(HttpServletRequest request){
        String header=request.getHeader("Authorization");
        return StringUtils.hasText(header)&&header.startsWith("Bearer ") ? header.substring(7):null;
    }
    public boolean validateToken(String token){
        try{return StringUtils.hasText(Jwts.parser().verifyWith(key).build()
            .parseSignedClaims(token).getPayload().getSubject());}
        catch(JwtException|IllegalArgumentException ex){return false;}
    }
    public String getUsernameOfToken(String token){
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }
    public String generateToken(Authentication auth){
        Date now=new Date();
        return Jwts.builder().subject(auth.getName()).issuedAt(now)
            .expiration(new Date(now.getTime()+expirationMs))
            .claim("rol",auth.getAuthorities().iterator().next().getAuthority())
            .signWith(key,Jwts.SIG.HS256).compact();
    }
    @SuppressWarnings("unchecked")
    public <T extends Actor> T userLogin(){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth==null||!auth.isAuthenticated())return null;
        return (T) actors.findByUsername(auth.getName()).orElse(null);
    }
}
