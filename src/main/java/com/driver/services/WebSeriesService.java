package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{
        if (webSeriesEntryDto== null) return 0;
        //Add a webSeries to the database and update the ratings of the productionHouse

        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        WebSeries webSeries = webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName());
        if (webSeries == null) {
            //use function written in Repository Layer for the same
            //Dont forget to save the production and webseries Repo
            webSeries = new WebSeries(webSeriesEntryDto.getSeriesName(), webSeriesEntryDto.getAgeLimit(),
                    webSeriesEntryDto.getRating(), webSeriesEntryDto.getSubscriptionType());
            webSeriesRepository.save(webSeries);

            ProductionHouse productionHouse = webSeries.getProductionHouse();
            double ratings = productionHouse.getRatings();
            int currentSize = productionHouse.getWebSeriesList().size();
            double newRatings = (ratings*currentSize + webSeries.getRating())/(currentSize + 1);
            productionHouse.getWebSeriesList().add(webSeries);
            productionHouse.setRatings(newRatings);
            return (int) newRatings;
        }else throw new Exception("Series is already present");
    }

}
