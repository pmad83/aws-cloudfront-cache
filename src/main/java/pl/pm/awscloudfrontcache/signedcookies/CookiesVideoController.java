package pl.pm.awscloudfrontcache.signedcookies;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import software.amazon.awssdk.services.cloudfront.cookie.CookiesForCustomPolicy;

/*
 * Kontroler obsługujący żądanie strony wideo, ustawiając ciasteczka CloudFront z niestandardową polityką dostępu.
 */
@Controller
public class CookiesVideoController {

    private final CloudFrontSignedCookiesService signedCookiesService;

    public CookiesVideoController(CloudFrontSignedCookiesService signedCookiesService) {
        this.signedCookiesService = signedCookiesService;
    }

    @GetMapping("/video-signedcookies")
    public String getVideoPage(HttpServletResponse response, Model model) throws Exception {
        CookiesForCustomPolicy cookies = signedCookiesService.generateSignedCookies();

        setCookie(response, model, cookies.policyHeaderValue());
        setCookie(response, model, cookies.signatureHeaderValue());
        setCookie(response, model, cookies.keyPairIdHeaderValue());

        model.addAttribute("videoUrl", signedCookiesService.generateUrl());

        return "video-signedcookies";
    }

    private void setCookie(HttpServletResponse response, Model model, String cookieString) {
        String[] cookieParts = cookieString.split("=", 2);

        if (cookieParts.length == 2) {

            ResponseCookie responseCookie = ResponseCookie
                    .from(cookieParts[0], cookieParts[1])
                    .secure(true)
                    .httpOnly(true)
                    .path("/")
                    .domain(signedCookiesService.getDomain())
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

            // Przekazanie ciasteczka do modelu, aby wyświetlić je na frontend
            model.addAttribute(cookieParts[0].replace("-", ""), responseCookie.toString());

        } else {
            throw new IllegalArgumentException("Nieprawidłowy format cookie: " + cookieString);
        }
    }
}
