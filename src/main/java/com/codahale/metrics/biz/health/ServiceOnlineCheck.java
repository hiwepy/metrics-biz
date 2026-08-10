/*
 * Copyright (c) 2018-present, easy-4-java (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codahale.metrics.biz.health;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import com.codahale.metrics.health.HealthCheck;

/**
 * Dropwizard {@link HealthCheck} that probes the reachability of a
 * remote IP address using {@link InetAddress#isReachable(int)} and
 * {@link InetAddress#isReachable(NetworkInterface, int, int)}.
 *
 * <p>The current {@link #check()} implementation is a placeholder that
 * returns {@code null}; the actual reachability routine lives in the
 * package-private {@link #isAddressAvailable(String, int)} helper which
 * performs the ICMP/echo probe and iterates over every available
 * network interface.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see <a href="http://blog.csdn.net/paullmq/article/details/9032631">Original CSDN post</a>
 */
public class ServiceOnlineCheck extends HealthCheck {

    /**
     * Placeholder implementation; reserved for the real probe.
     *
     * @return always {@code null}.
     * @throws Exception reserved for future implementations.
     */
    @Override
    protected Result check() throws Exception {

        return null;
    }

    /**
     * Performs a reachability probe of {@code ip} using the JVM's
     * default reachability routine, then iterates every
     * {@link NetworkInterface} to repeat the probe through each interface.
     * Diagnostic output is written to {@link System#out} for legacy
     * compatibility.
     *
     * @param ip the IPv4 or IPv6 address to probe.
     * @param timeout the probe timeout in milliseconds.
     */
    void isAddressAvailable(String ip,int timeout) {
        try {
            InetAddress address = InetAddress.getByName(ip);// ping this IP

            if (address instanceof java.net.Inet4Address) {
                System.out.println(ip + " is ipv4 address");
            } else if (address instanceof java.net.Inet6Address) {
                System.out.println(ip + " is ipv6 address");
            } else {
                System.out.println(ip + " is unrecongized");
            }

            if (address.isReachable(timeout)) {
                System.out.println("SUCCESS - ping " + ip + " with no interface specified");
            } else {
                System.out.println("FAILURE - ping " + ip + " with no interface specified");
            }

            System.out.println("\n-------Trying different interfaces--------\n");

            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                System.out.println("Checking interface, DisplayName:" + ni.getDisplayName() + ", Name:" + ni.getName());
                if (address.isReachable(ni, 0, 5000)) {
                    System.out.println("SUCCESS - ping " + ip);
                } else {
                    System.out.println("FAILURE - ping " + ip);
                }

                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    System.out.println("IP: " + ips.nextElement().getHostAddress());
                }
                System.out.println("-------------------------------------------");
            }
        } catch (Exception e) {
            System.out.println("error occurs.");
            e.printStackTrace();
        }
    }

}