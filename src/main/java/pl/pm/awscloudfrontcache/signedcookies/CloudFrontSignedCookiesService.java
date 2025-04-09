package pl.pm.awscloudfrontcache.signedcookies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.cookie.CookiesForCustomPolicy;
import software.amazon.awssdk.services.cloudfront.model.CustomSignerRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/*
 * Serwis odpowiedzialny za generowanie podpisanych ciasteczek (signed cookies) dla zasobów CloudFront z niestandardową polityką dostępu (custom policy).
 */
@Service
public class CloudFrontSignedCookiesService {

    private static final Logger logger = LoggerFactory.getLogger(CloudFrontSignedCookiesService.class);

    @Value("${cloudfront.domain}")
    private String cloudFrontDomain;

    @Value("${cloudfront.videoPath}")
    private String videoPath;

    @Value("${cloudfront.keyPairId}")
    private String keyPairId;

    @Value("${cloudfront.privateKeyPath}")
    private String privateKeyPath;

    public CookiesForCustomPolicy generateSignedCookies() throws Exception {
        Instant expirationDate = Instant.now().plus(30, ChronoUnit.MINUTES);

        CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();

        CustomSignerRequest customRequest = CustomSignerRequest.builder()
                .resourceUrl(generateUrl())
                .privateKey(new java.io.File(privateKeyPath).toPath())
                .keyPairId(keyPairId)
                .expirationDate(expirationDate)
                .build();
        CookiesForCustomPolicy cookiesForCustomPolicy = cloudFrontUtilities.getCookiesForCustomPolicy(customRequest);

        logger.info("CloudFront-Policy cookie: {}", cookiesForCustomPolicy.policyHeaderValue());
        logger.info("CloudFront-Key-Pair-Id cookie: {}", cookiesForCustomPolicy.keyPairIdHeaderValue());
        logger.info("CloudFront-Signature cookie: {}", cookiesForCustomPolicy.signatureHeaderValue());

        return cookiesForCustomPolicy;
    }

    public String generateUrl() {
        return "https://" + cloudFrontDomain + "/" + videoPath;
    }

    public String getDomain() {
        return cloudFrontDomain;
    }
}
