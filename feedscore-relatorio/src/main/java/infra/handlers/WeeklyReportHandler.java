package infra.handlers;

import application.usecases.WeeklyReportCreateUseCase;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

public class WeeklyReportHandler implements RequestHandler<ScheduledEvent, Void> {

    private static final Logger LOG = Logger.getLogger(WeeklyReportHandler.class);

    private final WeeklyReportCreateUseCase weeklyReportCreateUseCase;

    @Inject
    public WeeklyReportHandler(WeeklyReportCreateUseCase weeklyReportCreateUseCase) {
        this.weeklyReportCreateUseCase = weeklyReportCreateUseCase;
    }

    @Override
    public Void handleRequest(ScheduledEvent event, Context context) {
        LOG.info("Geração do relatório semanal iniciada");

        weeklyReportCreateUseCase.execute();

        LOG.info("Relatório semanal enviado");
        return null;
    }
}