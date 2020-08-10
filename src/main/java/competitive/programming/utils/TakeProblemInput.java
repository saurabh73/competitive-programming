package competitive.programming.utils;

import competitive.programming.models.ProblemInput;
import lombok.Getter;
import org.gradle.internal.impldep.com.fasterxml.jackson.databind.DeserializationFeature;
import org.gradle.internal.impldep.com.fasterxml.jackson.databind.ObjectMapper;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsEmpty;

import java.util.Scanner;

public class TakeProblemInput implements Take {

    @Getter
    private ProblemInput problemInput;

    @Override
    public Response act(Request req) {
        System.out.println("Request Received");
        try {
            ObjectMapper mapper = new ObjectMapper();
            Scanner scanner = new Scanner(req.body());
            StringBuilder body = new StringBuilder();
            while (scanner.hasNext()) {
                body.append(scanner.next());
            }
            System.out.println(body.toString());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            this.problemInput = mapper.readValue(body.toString(), ProblemInput.class);
            System.out.println(mapper.writeValueAsString(this.problemInput));
        }
        catch (Exception ex) {
            System.out.println("Error Happened");
            ex.printStackTrace();
        }
        return new RsEmpty();
    }
}
