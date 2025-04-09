package pl.pm.awscloudfrontcache.signedurl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/*
 * Serwis generujący podpisane adresy URL (signed URLs) do zasobów CloudFront z wykorzystaniem domyślnej polityki (canned policy).
 */
@Service
public class CloudFrontSignedUrlService {

    private static final Logger logger = LoggerFactory.getLogger(CloudFrontSignedUrlService.class);

    @Value("${cloudfront.domain}")
    private String cloudFrontDomain;

    @Value("${cloudfront.videoPath}")
    private String videoPath;

    @Value("${cloudfront.keyPairId}")
    private String keyPairId;

    @Value("${cloudfront.privateKeyPath}")
    private String privateKeyPath;

    public String generateSignedUrl() throws Exception {
        Instant expirationDate = Instant.now().plus(30, ChronoUnit.MINUTES);

        CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();

        CannedSignerRequest cannedRequest = CannedSignerRequest.builder()
                .resourceUrl(generateUrl())
                .privateKey(new java.io.File(privateKeyPath).toPath())
                .keyPairId(keyPairId)
                .expirationDate(expirationDate)
                .build();
        SignedUrl signedUrl = cloudFrontUtilities.getSignedUrlWithCannedPolicy(cannedRequest);

        String url = signedUrl.url();
        logger.info("SignedUrl: {}", url);

        return url;
    }

    private String generateUrl() {
        return "https://" + cloudFrontDomain + "/" + videoPath;
    }
}
