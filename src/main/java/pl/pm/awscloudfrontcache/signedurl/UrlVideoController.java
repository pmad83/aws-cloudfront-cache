package pl.pm.awscloudfrontcache.signedurl;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/*
 * Kontroler obsługujący żądanie strony wideo, udostępniając zasób CloudFront za pomocą podpisanego URL.
 */
@Controller
public class UrlVideoController {

    private final CloudFrontSignedUrlService signedUrlService;

    public UrlVideoController(CloudFrontSignedUrlService signedUrlService) {
        this.signedUrlService = signedUrlService;
    }

    @GetMapping("/video-signedurl")
    public String getVideoPage(Model model) throws Exception {
        String signedUrl = signedUrlService.generateSignedUrl();
        model.addAttribute("videoUrl", signedUrl);
        return "video-signedurl";
    }
}
