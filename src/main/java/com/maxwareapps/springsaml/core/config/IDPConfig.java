package com.maxwareapps.springsaml.core.config;

import org.apache.commons.httpclient.HttpClient;
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

@Configuration
public class IDPConfig {
    private Timer backgroundTaskTimer;

    private final ExtendedMetadata extendedMetadata;
    private final HttpClient httpClient;
    private final StaticBasicParserPool parserPool;

    @Autowired
    public IDPConfig(ExtendedMetadata extendedMetadata, HttpClient httpClient, StaticBasicParserPool parserPool) {
        this.extendedMetadata = extendedMetadata;
        this.httpClient = httpClient;
        this.parserPool = parserPool;
    }

    @PostConstruct
    public void init() {
        this.backgroundTaskTimer = new Timer(true);
    }

    @PreDestroy
    public void destroy() {
        this.backgroundTaskTimer.purge();
        this.backgroundTaskTimer.cancel();
    }

    @Bean
    @Qualifier("metadata")
    public CachingMetadataManager metadata() throws MetadataProviderException {
        List<MetadataProvider> providers = new ArrayList<MetadataProvider>();
        providers.add(ssoCircleExtendedMetadataProvider());
        return new CachingMetadataManager(providers);
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //                       Begin SSOCircle IDP
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Bean
    @Qualifier("idp-ssocircle")
    public ExtendedMetadataDelegate ssoCircleExtendedMetadataProvider()
            throws MetadataProviderException {
        String idpSSOCircleMetadataURL = "https://idp.ssocircle.com/idp-meta.xml";
        HTTPMetadataProvider httpMetadataProvider = new HTTPMetadataProvider(
                this.backgroundTaskTimer, httpClient, idpSSOCircleMetadataURL);
        httpMetadataProvider.setParserPool(parserPool);
        ExtendedMetadataDelegate extendedMetadataDelegate =
                new ExtendedMetadataDelegate(httpMetadataProvider, extendedMetadata);
        extendedMetadataDelegate.setMetadataTrustCheck(true);
        extendedMetadataDelegate.setMetadataRequireSignature(false);
        backgroundTaskTimer.purge();
        return extendedMetadataDelegate;
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //                        End SSOCircle IDP
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
}
