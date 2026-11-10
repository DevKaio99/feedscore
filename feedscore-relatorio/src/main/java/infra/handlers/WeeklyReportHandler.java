package infra.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import org.jboss.logging.Logger;

public class WeeklyReportHandler implements RequestHandler<ScheduledEvent, Void> {

    private static final Logger LOG = Logger.getLogger(WeeklyReportHandler.class);

    @Override
    public Void handleRequest(ScheduledEvent event, Context context) {
        LOG.info("Geração do relatório semanal iniciada");
        return null;
    }
}