package demo.entity;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * create by hanshin on 2021/4/19
 */
public class SlaveDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceThread.get();

    }
}
