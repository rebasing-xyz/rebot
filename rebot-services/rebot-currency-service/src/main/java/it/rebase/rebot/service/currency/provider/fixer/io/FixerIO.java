package it.rebase.rebot.service.currency.provider.fixer.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.rebase.rebot.service.currency.provider.fixer.io.pojo.Rates;
import it.rebase.rebot.service.currency.provider.fixer.io.pojo.ResponseBase;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.enterprise.context.ApplicationScoped;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.logging.Logger;

@ApplicationScoped
public class FixerIO {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public static final String FIXER_IO_BASE_URL = "https://api.fixer.io/";
    public static final String LATEST = "latest";
    public static final String SYMBOLS = "symbols";
    public static final String BASE = "base";

    public static final String DEFAULT_BASE_CURRENCY = "USD";
    public static final String DEFAULT_SYMBOLS = "BRL,GBP,EUR";

    public Object execute(String URL) {

        log.fine("performing request with the url " + URL);
        try {
            HttpGet request = new HttpGet(URL);
            ResponseHandler<String> responseHandler = (final HttpResponse response) -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(client().execute(request, responseHandler), ResponseBase.class);
        } catch (final Exception e) {
            e.printStackTrace();
            return "Failed to get information using URL " + URL + ", message: [" + e.getMessage() + "]";
        }


    }

    private CloseableHttpClient client() {
        RequestConfig config = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();
        return HttpClients.custom().setDefaultRequestConfig(config).build();
    }

}