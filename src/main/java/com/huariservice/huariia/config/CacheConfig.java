package com.huariservice.huariia.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

/**
 * In-memory Caffeine cache configuration with per-cache TTLs for external government APIs
 * and health probes, improving latency and resilience against upstream instability.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String IBGE_STATES = "ibgeStates";
    public static final String IBGE_MUNICIPALITIES = "ibgeMunicipalities";
    public static final String VIACEP_LOOKUPS = "viaCepLookups";
    public static final String SENADO_SENATORS = "senadoSenators";
    public static final String CAMARA_DEPUTIES = "camaraDeputies";
    public static final String CAMARA_EXPENSES = "camaraExpenses";
    public static final String CAMARA_PROPOSITIONS = "camaraPropositions";
    public static final String SENADO_MATTERS = "senadoMatters";
    public static final String AUDIT_COURT_PROCESSES = "auditCourtProcesses";
    public static final String AUDIT_COURT_IRREGULAR_ACCOUNTS = "auditCourtIrregularAccounts";
    public static final String AUDIT_COURT_OVERVIEW = "auditCourtOverview";
    public static final String INTEGRATIONS_HEALTH = "integrationsHealth";

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(
                buildCache(IBGE_STATES, Duration.ofHours(24), 10),
                buildCache(IBGE_MUNICIPALITIES, Duration.ofHours(24), 50),
                buildCache(VIACEP_LOOKUPS, Duration.ofHours(24), 1_000),
                buildCache(SENADO_SENATORS, Duration.ofMinutes(60), 200),
                buildCache(CAMARA_DEPUTIES, Duration.ofMinutes(45), 200),
                buildCache(CAMARA_EXPENSES, Duration.ofMinutes(15), 200),
                buildCache(CAMARA_PROPOSITIONS, Duration.ofMinutes(10), 200),
                buildCache(SENADO_MATTERS, Duration.ofMinutes(10), 200),
                buildCache(AUDIT_COURT_PROCESSES, Duration.ofMinutes(10), 100),
                buildCache(AUDIT_COURT_IRREGULAR_ACCOUNTS, Duration.ofMinutes(10), 100),
                buildCache(AUDIT_COURT_OVERVIEW, Duration.ofMinutes(5), 100),
                buildCache(INTEGRATIONS_HEALTH, Duration.ofMinutes(5), 10)
        ));
        return manager;
    }

    private CaffeineCache buildCache(String name, Duration ttl, long maxSize) {
        return new CaffeineCache(name, Caffeine.newBuilder()
                .expireAfterWrite(ttl)
                .maximumSize(maxSize)
                .recordStats()
                .build());
    }
}
