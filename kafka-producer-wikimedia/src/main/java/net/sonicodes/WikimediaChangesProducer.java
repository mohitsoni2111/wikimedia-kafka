package net.sonicodes;

import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.background.BackgroundEventHandler;
import com.launchdarkly.eventsource.background.BackgroundEventSource;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Service
public class WikimediaChangesProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikimediaChangesProducer.class);

    private KafkaTemplate<String, String> kafkaTemplate;

    public WikimediaChangesProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage() throws InterruptedException {
        String topic = "wikimedia_recentChange";

        // To read real time stream data from wikimedia, we use event source
        BackgroundEventHandler backgroundEventHandler = new WikimediaChangesHandler(kafkaTemplate, topic);
        String url = "https://stream.wikimedia.org/v2/stream/recentchange";
        BackgroundEventSource.Builder builder = new BackgroundEventSource.Builder(backgroundEventHandler, new EventSource.Builder(URI.create(url)));
        BackgroundEventSource source = builder.build();
        source.start();

        TimeUnit.MINUTES.sleep(10);

//        String topic = "wikimedia_recentChange";
//        String url = "https://stream.wikimedia.org/v2/stream/recentchange";
//
//        BackgroundEventHandler backgroundEventHandler = new WikimediaChangesHandler(kafkaTemplate, topic);
//
//        // OkHttpClient is required now
//        OkHttpClient client = new OkHttpClient();
//
//        EventSource.Builder eventSourceBuilder = new EventSource.Builder(URI.create(url));
//
//        BackgroundEventSource backgroundSource = new BackgroundEventSource.Builder(backgroundEventHandler, eventSourceBuilder).build();
//
//        backgroundSource.start();
    }
}
