/*
  The MIT License (MIT)

  Copyright (c) 2017 Rebase.it ReBot <just@rebase.it>

  Permission is hereby granted, free of charge, to any person obtaining a copy of
  this software and associated documentation files (the "Software"), to deal in
  the Software without restriction, including without limitation the rights to
  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
  the Software, and to permit persons to whom the Software is furnished to do so,
  subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package it.rebase.rebot.service.faq;

import it.rebase.rebot.api.emojis.Emoji;
import it.rebase.rebot.service.cache.qualifier.FaqCache;
import it.rebase.rebot.service.faq.pojo.Project;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.infinispan.Cache;

import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Stateless
@LocalBean
@Dependent
public class FaqService {

    public static final String JSON_SOURCE_LOCATION = "https://raw.githubusercontent.com/rebase-it/rebot/master/rebot-services/rebot-faq-service/src/main/resources/META-INF/faq-properties.json";

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final String PROJECT_NOT_FOUND_MESSAGE = "Ooops, I didn't find any project with the name <b>%s</b>. " + Emoji.DISAPPOINTED_FACE;

    @Inject
    @FaqCache(classToIndex = "it.rebase.rebot.service.faq.pojo.Project")
    private Cache<String, Project> cache;


    @Schedule(minute = "*/30", hour = "*", persistent = false)
    public void populateCache() {
        try {
            log.fine("Trying to read the file " + JSON_SOURCE_LOCATION);
            URL jsonUrl = new URL(JSON_SOURCE_LOCATION);

            ObjectMapper mapper = new ObjectMapper();
            List<Project> myObjects = mapper.readValue(jsonUrl, new TypeReference<List<Project>>() {
            });

            myObjects.stream().forEach(project -> {
                log.fine("Add entry in the cache: [" + project.getId() + " - " + project.id + "]");
                cache.putIfAbsent(project.getId(), project);
            });
            log.fine("Cache successfully populated.");
            myObjects.clear();
        } catch (Exception e) {
            log.warning("Failed to populate the cache: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @param key term to be searched
     * @return the result, if the key is not found a msg informing that it was not found is returned
     */
    public String query(String key) {
        StringBuilder stbuilder = new StringBuilder();

        List<Project> cacheEntries = cache.values().stream()
                .filter(item -> item instanceof Project)
                .collect(Collectors.toList());

        for (Project project : cacheEntries.stream().filter(item -> item.getId().contains(key)).collect(Collectors.toList())) {
            stbuilder.append(project.toString());
            stbuilder.append(" - ");
            stbuilder.append(project.getDescription() + "\n");
        }

        if (stbuilder.length() <= 0) {
            stbuilder.append(String.format(PROJECT_NOT_FOUND_MESSAGE, key));
        }

        return stbuilder.toString();
    }

}