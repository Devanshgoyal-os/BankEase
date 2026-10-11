package com.bankease;
import io.jsonwebtoken.*; import io.jsonwebtoken.security.Keys; import jakarta.servlet.*; import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.*; import org.springframework.context.annotation.*; import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity; import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy; import org.springframework.security.core.authority.SimpleGrantedAuthority; import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain; import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean; import org.springframework.web.cors.*; import org.springframework.web.filter.OncePerRequestFilter; import javax.crypto.SecretKey; import java.io.IOException; import java.util.*;
@Component class JwtUtil { SecretKey key; JwtUtil(@Value("${jwt.secret}") String s){ key=Keys.hmacShaKeyFor(s.getBytes()); }
  String make(String u,String role){ return Jwts.builder().subject(u).claim("role",role).expiration(new Date(System.currentTimeMillis()+1800000)).signWith(key).compact(); }
  Claims parse(String t){ return Jwts.parser().verifyWith(key).build().parseSignedClaims(t).getPayload(); } }
@Component class JwtFilter extends OncePerRequestFilter { @Autowired JwtUtil jwt;
  protected void doFilterInternal(HttpServletRequest rq,HttpServletResponse rs,FilterChain c) throws ServletException,IOException {
    String h=rq.getHeader("Authorization");
    if(h!=null&&h.startsWith("Bearer ")) try{ Claims cl=jwt.parse(h.substring(7));
      SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(cl.getSubject(),null,List.of(new SimpleGrantedAuthority("ROLE_"+cl.get("role"))))); }catch(Exception e){}
    c.doFilter(rq,rs); } }
@Configuration @EnableWebSecurity class SecurityConfig {
  @Bean SecurityFilterChain chain(HttpSecurity http,JwtFilter f) throws Exception {
    http.csrf(c->c.disable()).cors(Customizer.withDefaults()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(a->a.requestMatchers("/api/auth/**","/error").permitAll().requestMatchers("/api/admin/**").hasRole("ADMIN").anyRequest().authenticated())
      .addFilterBefore(f,UsernamePasswordAuthenticationFilter.class); return http.build(); }
  @Bean FilterRegistrationBean<JwtFilter> jwtFilterOff(JwtFilter f){ FilterRegistrationBean<JwtFilter> r=new FilterRegistrationBean<>(f); r.setEnabled(false); return r; }
  @Bean PasswordEncoder encoder(){ return new BCryptPasswordEncoder(); }
  @Bean CorsConfigurationSource corsConfigurationSource(){ CorsConfiguration c=new CorsConfiguration(); c.setAllowedOriginPatterns(List.of("http://localhost:*","http://127.0.0.1:*","http://192.168.*.*:*","http://10.*.*.*:*","http://172.*.*.*:*")); c.setAllowedMethods(List.of("*")); c.setAllowedHeaders(List.of("*"));
    UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource(); s.registerCorsConfiguration("/**",c); return s; }
}
