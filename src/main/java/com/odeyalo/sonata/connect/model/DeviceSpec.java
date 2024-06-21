package com.odeyalo.sonata.connect.model;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

/**
 * Specification that describes a device that connected to Sonata-Connect
 */
public interface DeviceSpec {
    /**
     * @return Unique identifier for this device
     */
    @NotNull
    String getId();

    /**
     * @return a name of this device that will be shown for the user
     */
    @NotNull
    String getName();

    /**
     * @return type of the device
     */
    @NotNull
    DeviceType getType();

    /**
     * @return volume that applied for this device
     */
    @NotNull
    Volume getVolume();

    /**
     * @return current status of the device to check if device is active or no
     */
    @NotNull
    DeviceStatus getStatus();

    /**
     * @return {@code true} if this device is active, {@code false} otherwise
     */
    default boolean isActive() {
        return getStatus().isActive();
    }

    /**
     * @return {@code true} if this device is idle, {@code false} otherwise
     */
    default boolean isIdle() {
        return getStatus().isIdle();
    }

    /**
     * Represent a volume for the device
     * Volume MUST BE in range from 0 to 100
     *
     * @param value - an integer that represent a volume
     */
    record Volume(int value) {
        /**
         * @throws IllegalStateException if a volume is in invalid range
         */
        public Volume {
            Assert.state(value >= 0, "Volume cannot be negative!");
            Assert.state(value <= 100, "Volume must be in range 0 - 100!");
        }

        public static Volume from(int value) {
            return new Volume(value);
        }

        public int asInt() {
            return value;
        }
    }

    /**
     * Represent a current status of the device.
     */
    enum DeviceStatus {
        /**
         * Device is active, means that current device is used to play something
         */
        ACTIVE,
        /**
         * Device is in inactive state, means that nothing is playing on it
         */
        IDLE;

        /**
         * Factory method to create a {@link DeviceStatus} from {@code boolean}
         *
         * @param status - {@code boolean} representation of status
         * @return -  {@link #ACTIVE} if {@code true} was as input, {@link #IDLE} otherwise
         */
        public static DeviceStatus fromBoolean(final boolean status) {
            return status ? ACTIVE : IDLE;
        }

        public boolean isActive() {
            return this == ACTIVE;
        }

        public boolean isIdle() {
            return this == IDLE;
        }
    }
}
