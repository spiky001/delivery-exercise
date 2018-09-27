




package ws.rest.auth;





import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;





@Secured
@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

    public static final String AUTHENTICATION_HEADER = "Authorization";





    @Override
    public ContainerRequest filter(ContainerRequest containerRequest) {

        // TODO: Review actions filter configuration
        if (containerRequest.getMethod().equals("GET")) {
            // all users have access to use GET action
            return containerRequest;
        }

        String authCredentials = containerRequest.getHeaderValue(AUTHENTICATION_HEADER);

        AuthenticationService authenticationService = new AuthenticationService();

        boolean authenticationStatus = authenticationService.authenticate(authCredentials);

        if (!authenticationStatus) {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                                                      .entity("Invalid Authentication")
                                                      .build());
        }
        return containerRequest;

    }

}