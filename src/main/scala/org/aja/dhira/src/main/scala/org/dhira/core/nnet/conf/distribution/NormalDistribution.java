/*
 *
 *  * Copyright 2015 Skymind,Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package org.deeplearning4j.nn.conf.distribution;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A normal distribution.
 *
 */
public class NormalDistribution extends Distribution {

    private double mean, std;

    /**
     * Create a normal distribution
     * with the given mean and std
     *
     * @param mean the mean
     * @param std  the standard deviation
     */
    @JsonCreator
    public NormalDistribution(@JsonProperty("mean") double mean, @JsonProperty("std") double std) {
        this.mean = mean;
        this.std = std;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getStd() {
        return std;
    }

    public void setStd(double std) {
        this.std = std;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(mean);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(std);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NormalDistribution other = (NormalDistribution) obj;
        if (Double.doubleToLongBits(mean) != Double
                .doubleToLongBits(other.mean))
            return false;
        if (Double.doubleToLongBits(std) != Double.doubleToLongBits(other.std))
            return false;
        return true;
    }

    public String toString() {
        return "NormalDistribution{" +
                "mean=" + mean +
                ", std=" + std +
                '}';
    }
}
