package com.palmap.BluetoothUtils.test;

import com.palmap.BluetoothUtils.main.model.Beacon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2015/11/9.
 */
public class TestData {

  public static List<Beacon> getTestBeacons(){
    List<Beacon> beacons = new ArrayList<Beacon>();
    Beacon beacon = null;
    for(int i = 0; i < 20; i++){
      beacon = new Beacon();
      beacon.setId(i);
      beacon.setUuid("23A01AF0-232A-4518-9C0E-323FB773F5EF");
      beacon.setMajor(10000);
      beacon.setMinor(i);
      beacon.setY(1.3526572E7 + (4 * i) % 31);
      beacon.setY(3663418.5 + (4 * i) % 27);
      beacon.setFloorId(1672);
      beacons.add(beacon);
    }
    return beacons;
  }

}
