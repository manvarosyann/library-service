package com.library.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "my")
public class DateTimeFormatProperties {
     private List<String> dateFormats;
     private List<String> timeFormats;

     public List<String> getDateFormats() {
         return dateFormats;
     }

     public void setDateFormats(List<String> dateFormats) {
         this.dateFormats = dateFormats;
     }

     public List<String> getTimeFormats() {
         return timeFormats;
     }

     public void setTimeFormats(List<String> timeFormats) {
         this.timeFormats = timeFormats;
     }
}
