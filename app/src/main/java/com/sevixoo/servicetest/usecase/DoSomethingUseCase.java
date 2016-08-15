package com.sevixoo.servicetest.usecase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seweryn on 14.08.2016.
 */
public class DoSomethingUseCase implements UseCase<List<String>> {

    private String mParam1;

    public DoSomethingUseCase( String param1 ) {
        mParam1 = param1;
    }

    @Override
    public List<String> execute()throws Exception {
        List<String> list = new ArrayList<>();
        for (int i = 0 ; i < 10 ; i++){
            Thread.sleep(1000);
            list.add(mParam1+" " + i);
        }
        return list;
    }
}
