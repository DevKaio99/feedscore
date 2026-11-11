package application.usecases;

import application.gateways.EmailReport;
import application.gateways.EmailSender;
import domain.enums.Urgency;
import domain.models.FeedbackSummary;
import domain.repositories.FeedbackReader;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.Collectors;

public class WeeklyReportCreateUseCase {

    private static final ZoneId ZONE_SAO_PAULO = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Locale PT_BR = Locale.of("pt", "BR");
    private static final String SUBJECT = "Relatório semanal de avaliações - %s";

    private final FeedbackReader feedbackReader;
    private final EmailSender emailSender;
    private final Clock clock;

    public WeeklyReportCreateUseCase(FeedbackReader feedbackReader, EmailSender emailSender, Clock clock) {
        this.feedbackReader = feedbackReader;
        this.emailSender = emailSender;
        this.clock = clock;
    }

    public void execute() {
        String pastWeek = calculatePastWeek();
        List<FeedbackSummary> feedbacks = feedbackReader.findByWeek(pastWeek);

        if (feedbacks.isEmpty()) {
            String emptyBody = """
                    Relatório semanal!
                    Semana: %s

                    Nenhuma avaliação foi recebida no período.
                    """.formatted(pastWeek);

            emailSender.send(new EmailReport(SUBJECT.formatted(pastWeek), emptyBody));
            return;
        }

        double media = feedbacks.stream()
                .mapToInt(FeedbackSummary::score)
                .average()
                .orElse(0.0);

        Map<LocalDate, Long> porDia = feedbacks.stream()
                .collect(Collectors.groupingBy(
                        feedback -> feedback.createdAt().atZone(ZONE_SAO_PAULO).toLocalDate(),
                        TreeMap::new,
                        Collectors.counting()));

        String listaPorDia = porDia.entrySet().stream()
                .map(dia -> "- %s: %d".formatted(dia.getKey().format(DAY_FORMAT), dia.getValue()))
                .collect(Collectors.joining("\n"));

        String listaPorUrgencia = Arrays.stream(Urgency.values())
                .map(urgency -> "- %s: %d".formatted(
                        urgency.getLabel(),
                        feedbacks.stream().filter(feedback -> feedback.urgency() == urgency).count()))
                .collect(Collectors.joining("\n"));

        String listaFeedbacks = feedbacks.stream()
                .map(feedback -> "- %s | %s | %s".formatted(
                        feedback.createdAt().atZone(ZONE_SAO_PAULO).format(DATE_FORMAT),
                        feedback.urgency().getLabel(),
                        feedback.description()))
                .collect(Collectors.joining("\n"));

        String emailBody = """
                Relatório semanal!
                Semana: %s

                Total de avaliações: %d
                Média de avaliações: %s

                Avaliações por dia:
                %s

                Avaliações por urgência:
                %s

                Avaliações recebidas:
                %s
                """.formatted(
                pastWeek,
                feedbacks.size(),
                String.format(PT_BR, "%.1f", media),
                listaPorDia,
                listaPorUrgencia,
                listaFeedbacks);

        emailSender.send(new EmailReport(SUBJECT.formatted(pastWeek), emailBody));
    }

    private String calculatePastWeek() {
        ZonedDateTime onePastWeek = ZonedDateTime.now(clock)
                .withZoneSameInstant(ZONE_SAO_PAULO)
                .minusWeeks(1);

        int year = onePastWeek.get(IsoFields.WEEK_BASED_YEAR);
        int week = onePastWeek.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        return "%d-W%02d".formatted(year, week);
    }
}