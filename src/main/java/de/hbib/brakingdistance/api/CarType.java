package de.hbib.brakingdistance.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Oualid Hbib
 */
@RequiredArgsConstructor
public enum CarType {

    RED_CAR("red_car.png", "Rotes Auto"),
    YELLOW_CAR("yellow_car.png", "Gelbes Auto"),
    BLUE_CAR("blue_car.png", "Blaues Auto"),
    RED_YELLOW_SMALL_TRUCK("red_yellow_small_truck.png", "Rotgelber Lastwagen"),
    YELLOW_TRUCK("yellow_truck.png", "Gelber Lastwagen");

    @Getter private final String fileName;
    @Getter private final String displayName;
}
