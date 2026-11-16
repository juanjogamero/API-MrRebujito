package mrRebujito.MrRebujito.security;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import mrRebujito.MrRebujito.service.ActorService;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final JWTUtils jwt;
    private final ActorService actors;
    public JWTAuthenticationFilter(JWTUtils jwt,ActorService actors){this.jwt=jwt;this.actors=actors;}
    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)
        throws ServletException,IOException {
        String token=jwt.getToken(request);
        if(token!=null&&jwt.validateToken(token)&&SecurityContextHolder.getContext().getAuthentication()==null){
            try{
                UserDetails user=actors.loadUserByUsername(jwt.getUsernameOfToken(token));
                if(user.isEnabled()&&user.isAccountNonLocked()){
                    UsernamePasswordAuthenticationToken authentication=
                        new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }catch(Exception ex){SecurityContextHolder.clearContext();}
        }
        chain.doFilter(request,response);
    }
}
