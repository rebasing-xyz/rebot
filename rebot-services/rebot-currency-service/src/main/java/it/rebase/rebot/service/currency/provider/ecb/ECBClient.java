package it.rebase.rebot.service.currency.provider.ecb;

import it.rebase.rebot.api.conf.systemproperties.BotProperty;
import it.rebase.rebot.service.cache.qualifier.CurrencyCache;
import it.rebase.rebot.service.persistence.pojo.Cube;
import it.rebase.rebot.service.persistence.repository.EcbRepository;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.infinispan.Cache;

import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Singleton
public class ECBClient {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public static final String ECB_XML_ADDRESS = "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    @Inject
    private EcbSaxHandler handler;

    @Inject
    private EcbRepository repository;

    @Resource
    TimerService timerService;

    @Inject
    @CurrencyCache
    private Cache<String, Object> cache;

    @Inject
    @BotProperty(name = "it.rebase.rebot.scheduler.timezone")
    String timezone;

    synchronized public void startTimer() {
        ScheduleExpression schedule = new ScheduleExpression();
        if (null == timezone) {
            log.warning("Timezone not set, using default: CET");
            schedule.timezone("CET");
        } else {
            schedule.timezone(timezone);
        }
        schedule.hour("15");
        schedule.minute("00");
        timerService.createCalendarTimer(schedule);
    }

    public void getAndPersistDailyCurrencies() {
        try {
            log.fine("Parsing currencies from " + ECB_XML_ADDRESS);
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            HttpGet httpReq = new HttpGet(ECB_XML_ADDRESS);
            HttpResponse response = client().execute(httpReq);
            saxParser.parse(response.getEntity().getContent(), handler);
            repository.persist(handler.cubes());
            cache.clear();
            handler.cubes().getCubes().forEach(cube -> {
                cache.put(cube.getCurrency(), cube, 24, TimeUnit.HOURS);
                cache.put("time", handler.cubes().getTime(), 24, TimeUnit.HOURS);
            });
        } catch (final Exception e) {
            log.severe("Error to retrieve currency rates from " + ECB_XML_ADDRESS + " - message: " + e.getMessage());
        }
    }

    @Timeout
    private void scheduler(Timer timer) {
        getAndPersistDailyCurrencies();
        log.fine("Timer executed, next timeout [" + timer.getNextTimeout() + "]");
    }


    private CloseableHttpClient client() {
        RequestConfig config = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();
        return HttpClients.custom().setDefaultRequestConfig(config).build();
    }

}