package com.rjay.wspider;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Collection;
import java.util.List;

@Controller
@ComponentScan
@Configuration
@EnableScheduling
@EnableAutoConfiguration
@ImportResource(locations = {"classpath*:app.xml"})
public class AppMain implements ApplicationContextAware {

    private final static Logger log = LoggerFactory.getLogger(AppMain.class);

    private final static int retention = 86400 * 1000 * 3;

    private final static List<Runnable> preHaltTasks = Lists.newArrayList();

    private static ApplicationContext context;

    public static ApplicationContext context() {
        return context;
    }

    private static boolean halt = false;

    @Autowired
    Environment environment;

    @Value("${server.tomcat.accesslog.enabled}")
    boolean accessLogEnabled;

    @Value("${server.tomcat.accesslog.directory}")
    String accessLogPath;

    @RequestMapping("/ok.htm")
    @ResponseBody
    String ok(@RequestParam(defaultValue = "false") String down, final HttpServletResponse response) {
        if (halt) {
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            return "halting";
        }
        if (Boolean.parseBoolean(down) && !halt) {
            log.warn("prehalt initiated and further /ok.htm request will return with status 503");
            halt = true;
            for (final Runnable r : preHaltTasks) {
                try {
                    r.run();
                } catch (Exception e) {
                    log.error("prehalt task failed", e);
                }
            }
        }
        return "ok";
    }

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "ok";
    }

    @Scheduled(cron = " 0 5 0 * * ? ") //runs every day 00:05:00
    public void accessLogCleaner() {
        if (accessLogEnabled) {
            if (StringUtils.isEmpty(accessLogPath)) {
                return;
            }
            log.warn("now cleaning access log in dir {}", accessLogPath);
            final Collection<File> files = FileUtils.listFiles(new File(accessLogPath), new String[]{"log"}, false);
            if (CollectionUtils.isEmpty(files)) {
                log.warn("no log found and nothing to do");
                return;
            }
            for (final File f : files) {
                if (f.getName().startsWith("access_log") && System.currentTimeMillis() - f.lastModified() > retention) {
                    final boolean b = f.delete();
                    log.warn("deleting old log {} ... {}", f.getName(), b);
                }
            }
        }
    }

    public static void addPreHaltTask(final Runnable runnable) {
        if (runnable != null) {
            preHaltTasks.add(runnable);
        }
    }

    public static void main(String[] args) throws Exception {
        log.warn("wspider started");
        try {
            SpringApplication.run(AppMain.class, args);
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (AppMain.context == null) {
            AppMain.context = applicationContext;
        }
    }
}

