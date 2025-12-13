package es.udc.ws.app.client.ui;

import es.udc.ws.app.client.service.ClientEncuestaService;
import es.udc.ws.app.client.service.ClientEncuestaServiceFactory;
import es.udc.ws.app.client.service.dto.ClientEncuestaDto;
import es.udc.ws.app.client.service.dto.ClientRespuestaDto;
import es.udc.ws.app.client.service.exceptions.ClientEncuestaFinalizadaException;
import es.udc.ws.app.client.service.exceptions.ClientEncuestaCanceladaException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import java.util.List;
import java.time.LocalDateTime;

public class AppServiceClient {
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsageAndExit();
        }

        ClientEncuestaService service =
                ClientEncuestaServiceFactory.getService();

        //addSurvey <question> <close_date>
        if ("addSurvey".equalsIgnoreCase(args[0])) {

            validateArgs(args, 3, new int[] {});

            try {
                ClientEncuestaDto encuesta =
                        new ClientEncuestaDto(
                                null,
                                args[1],
                                LocalDateTime.parse(args[2]),
                                false,
                                0,
                                0
                        );

                Long encuestaId = service.crearEncuesta(encuesta);

                System.out.println("Survey " + encuestaId +
                        " created successfully");

            } catch (InputValidationException ex) {
                System.err.println("Error de validación (addSurvey): " + ex.getMessage());
            } catch (Exception ex) {
                System.err.println("Error inesperado (addSurvey): " + ex.getMessage());
            }


        //answer <userEmail> <surveyId> <answer>
        } else if ("answer".equalsIgnoreCase(args[0])) {

            validateArgs(args, 4, new int[] {2});

            try {
                Long respuestaId =
                        service.responderEncuesta(
                                Long.parseLong(args[2]),
                                args[1],
                                Boolean.parseBoolean(args[3])
                        );

                System.out.println("Answer " + respuestaId +
                        " created successfully");

            } catch (ClientEncuestaFinalizadaException |
                     ClientEncuestaCanceladaException |
                     InstanceNotFoundException ex) {
                System.err.println("Error (answer): " + ex.getMessage());
            } catch (Exception ex) {
                System.err.println("Error inesperado (answer): " + ex.getMessage());
            }


        //cancel <surveyId>
        } else if ("cancel".equalsIgnoreCase(args[0])) {

            validateArgs(args, 2, new int[] {1});

            try {
                service.cancelarEncuesta(Long.parseLong(args[1]));

                System.out.println("Survey " + args[1] +
                        " cancelled successfully");

            } catch (ClientEncuestaFinalizadaException |
                     ClientEncuestaCanceladaException |
                     InstanceNotFoundException ex) {
                System.err.println("Error (cancel): " + ex.getMessage());
            } catch (Exception ex) {
                System.err.println("Error inesperado (cancel): " + ex.getMessage());
            }


        //findSurveys <keyword>
        } else if ("findSurveys".equalsIgnoreCase(args[0])) {

            validateArgs(args, 2, new int[] {});

            try {
                List<ClientEncuestaDto> encuestas =
                        service.buscarEncuestas(args[1], true);

                System.out.println("Found " + encuestas.size() +
                        " survey(s)");

                for (ClientEncuestaDto e : encuestas) {
                    System.out.println(e);
                }

            } catch (Exception ex) {
                System.err.println("Error inesperado (findSurveys): " + ex.getMessage());
            }

        //findSurvey <surveyId>
        } else if ("findSurvey".equalsIgnoreCase(args[0])) {

            validateArgs(args, 2, new int[] {1});

            try {
                ClientEncuestaDto encuesta =
                        service.buscarEncuestaPorId(
                                Long.parseLong(args[1]));

                System.out.println(encuesta);

            } catch (InstanceNotFoundException ex) {
                System.err.println("Error (findSurvey): " + ex.getMessage());
            } catch (Exception ex) {
                System.err.println("Error inesperado (findSurvey): " + ex.getMessage());
            }


        //findAnswers <surveyId> <onlyAffirmative>
        } else if ("findAnswers".equalsIgnoreCase(args[0])) {

            validateArgs(args, 3, new int[] {1});

            try {
                List<ClientRespuestaDto> respuestas =
                        service.obtenerRespuestas(
                                Long.parseLong(args[1]),
                                Boolean.parseBoolean(args[2])
                        );

                System.out.println("Found " + respuestas.size() +
                        " answer(s)");

                for (ClientRespuestaDto r : respuestas) {
                    System.out.println(r);
                }

            } catch (InstanceNotFoundException ex) {
                System.err.println("Error (findAnswers): " + ex.getMessage());
            } catch (Exception ex) {
                System.err.println("Error inesperado (findAnswers): " + ex.getMessage());
            }

        } else {
            printUsageAndExit();
        }
    }

    private static void validateArgs(String[] args,
                                     int expectedArgs,
                                     int[] numericArguments) {

        if (args.length != expectedArgs) {
            printUsageAndExit();
        }

        for (int position : numericArguments) {
            try {
                Double.parseDouble(args[position]);
            } catch (NumberFormatException e) {
                printUsageAndExit();
            }
        }
    }

    private static void printUsageAndExit() {
        printUsage();
        System.exit(-1);
    }

    private static void printUsage() {
        System.err.println(
                "Usage:\n" +
                        "  addSurvey <question> <close_date>\n" +
                        "  answer <userEmail> <surveyId> <true|false>\n" +
                        "  cancel <surveyId>\n" +
                        "  findSurveys <keyword>\n" +
                        "  findSurvey <surveyId>\n" +
                        "  findAnswers <surveyId> <true|false>\n"
        );
    }
}