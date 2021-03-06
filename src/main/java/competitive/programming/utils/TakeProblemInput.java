package competitive.programming.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import competitive.programming.models.ProblemInput;
import lombok.Getter;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqPrint;
import org.takes.rs.RsEmpty;

public class TakeProblemInput implements Take {

    @Getter
    private ProblemInput problemInput;

    @Override
    public Response act(final Request req) throws Exception {
        final String body = new RqPrint(req).printBody();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.problemInput = mapper.readValue(body, ProblemInput.class);
        return new RsEmpty();
    }
}
