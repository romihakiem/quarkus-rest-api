package com.skeleton.api.config;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.jboss.logging.Logger;

/**
 * Quarkus already performs graceful shutdown on its own: `quarkus.shutdown.timeout`
 * (see application.yml) tells Quarkus how long to wait for in-flight HTTP
 * requests to finish after a SIGTERM/Ctrl+C before the process actually exits.
 *
 * This listener just makes the sequence observable and gives a single place
 * to release any extra resources (custom thread pools, schedulers, open
 * file handles, external connections, etc.) before the JVM exits.
 */
@ApplicationScoped
public class GracefulShutdownListener {
    private static final Logger LOG = Logger.getLogger(GracefulShutdownListener.class);

    void onStart(@Observes StartupEvent event) {
        LOG.info(">>> Application started. Ready to accept requests.");
    }

    void onStop(@Observes ShutdownEvent event) {
        LOG.info(">>> Shutdown signal received. Waiting for in-flight requests to complete...");
        // Close any custom resources here, e.g.:
        // customExecutorService.shutdown();
        // scheduledJobRegistry.stopAll();
        LOG.info(">>> Cleanup complete. Bye!");
    }
}
