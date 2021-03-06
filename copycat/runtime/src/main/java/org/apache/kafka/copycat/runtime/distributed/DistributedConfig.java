/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package org.apache.kafka.copycat.runtime.distributed;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.copycat.runtime.WorkerConfig;

import java.util.Map;

import static org.apache.kafka.common.config.ConfigDef.Range.atLeast;

public class DistributedConfig extends WorkerConfig {
    private static final ConfigDef CONFIG;

    /*
     * NOTE: DO NOT CHANGE EITHER CONFIG STRINGS OR THEIR JAVA VARIABLE NAMES AS
     * THESE ARE PART OF THE PUBLIC API AND CHANGE WILL BREAK USER CODE.
     */

    /**
     * <code>group.id</code>
     */
    public static final String GROUP_ID_CONFIG = "group.id";
    private static final String GROUP_ID_DOC = "A unique string that identifies the Copycat cluster group this worker belongs to.";

    /**
     * <code>session.timeout.ms</code>
     */
    public static final String SESSION_TIMEOUT_MS_CONFIG = "session.timeout.ms";
    private static final String SESSION_TIMEOUT_MS_DOC = "The timeout used to detect failures when using Kafka's group management facilities.";

    /**
     * <code>heartbeat.interval.ms</code>
     */
    public static final String HEARTBEAT_INTERVAL_MS_CONFIG = "heartbeat.interval.ms";
    private static final String HEARTBEAT_INTERVAL_MS_DOC = "The expected time between heartbeats to the group coordinator when using Kafka's group management facilities. Heartbeats are used to ensure that the worker's session stays active and to facilitate rebalancing when new members join or leave the group. The value must be set lower than <code>session.timeout.ms</code>, but typically should be set no higher than 1/3 of that value. It can be adjusted even lower to control the expected time for normal rebalances.";

    /**
     * <code>worker.sync.timeout.ms</code>
     */
    public static final String WORKER_SYNC_TIMEOUT_MS_CONFIG = "worker.sync.timeout.ms";
    private static final String WORKER_SYNC_TIMEOUT_MS_DOC = "When the worker is out of sync with other workers and needs" +
            " to resynchronize configurations, wait up to this amount of time before giving up, leaving the group, and" +
            " waiting a backoff period before rejoining.";

    /**
     * <code>group.unsync.timeout.ms</code>
     */
    public static final String WORKER_UNSYNC_BACKOFF_MS_CONFIG = "worker.unsync.backoff.ms";
    private static final String WORKER_UNSYNC_BACKOFF_MS_DOC = "When the worker is out of sync with other workers and " +
            " fails to catch up within worker.sync.timeout.ms, leave the Copycat cluster for this long before rejoining.";
    public static final int WORKER_UNSYNC_BACKOFF_MS_DEFAULT = 5 * 60 * 1000;

    static {
        CONFIG = baseConfigDef()
                .define(GROUP_ID_CONFIG, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, GROUP_ID_DOC)
                .define(SESSION_TIMEOUT_MS_CONFIG,
                        ConfigDef.Type.INT,
                        30000,
                        ConfigDef.Importance.HIGH,
                        SESSION_TIMEOUT_MS_DOC)
                .define(HEARTBEAT_INTERVAL_MS_CONFIG,
                        ConfigDef.Type.INT,
                        3000,
                        ConfigDef.Importance.HIGH,
                        HEARTBEAT_INTERVAL_MS_DOC)
                .define(CommonClientConfigs.METADATA_MAX_AGE_CONFIG,
                        ConfigDef.Type.LONG,
                        5 * 60 * 1000,
                        atLeast(0),
                        ConfigDef.Importance.LOW,
                        CommonClientConfigs.METADATA_MAX_AGE_DOC)
                .define(CommonClientConfigs.CLIENT_ID_CONFIG,
                        ConfigDef.Type.STRING,
                        "",
                        ConfigDef.Importance.LOW,
                        CommonClientConfigs.CLIENT_ID_DOC)
                .define(CommonClientConfigs.SEND_BUFFER_CONFIG,
                        ConfigDef.Type.INT,
                        128 * 1024,
                        atLeast(0),
                        ConfigDef.Importance.MEDIUM,
                        CommonClientConfigs.SEND_BUFFER_DOC)
                .define(CommonClientConfigs.RECEIVE_BUFFER_CONFIG,
                        ConfigDef.Type.INT,
                        32 * 1024,
                        atLeast(0),
                        ConfigDef.Importance.MEDIUM,
                        CommonClientConfigs.RECEIVE_BUFFER_DOC)
                .define(CommonClientConfigs.RECONNECT_BACKOFF_MS_CONFIG,
                        ConfigDef.Type.LONG,
                        50L,
                        atLeast(0L),
                        ConfigDef.Importance.LOW,
                        CommonClientConfigs.RECONNECT_BACKOFF_MS_DOC)
                .define(CommonClientConfigs.RETRY_BACKOFF_MS_CONFIG,
                        ConfigDef.Type.LONG,
                        100L,
                        atLeast(0L),
                        ConfigDef.Importance.LOW,
                        CommonClientConfigs.RETRY_BACKOFF_MS_DOC)
                .define(CommonClientConfigs.METRICS_SAMPLE_WINDOW_MS_CONFIG,
                        ConfigDef.Type.LONG,
                        30000,
                        atLeast(0),
                        ConfigDef.Importance.LOW,
                        CommonClientConfigs.METRICS_SAMPLE_WINDOW_MS_DOC)
                .define(CommonClientConfigs.METRICS_NUM_SAMPLES_CONFIG,
                        ConfigDef.Type.INT,
                        2,
                        atLeast(1),
                        ConfigDef.Importance.LOW,
                        CommonClientConfigs.METRICS_NUM_SAMPLES_DOC)
                .define(CommonClientConfigs.METRIC_REPORTER_CLASSES_CONFIG,
                        ConfigDef.Type.LIST,
                        "",
                        ConfigDef.Importance.LOW,
                        CommonClientConfigs.METRIC_REPORTER_CLASSES_DOC)
                .define(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, ConfigDef.Type.STRING, CommonClientConfigs.DEFAULT_SECURITY_PROTOCOL, ConfigDef.Importance.MEDIUM, CommonClientConfigs.SECURITY_PROTOCOL_DOC)
                .define(SslConfigs.PRINCIPAL_BUILDER_CLASS_CONFIG, ConfigDef.Type.CLASS, SslConfigs.DEFAULT_PRINCIPAL_BUILDER_CLASS, ConfigDef.Importance.LOW, SslConfigs.PRINCIPAL_BUILDER_CLASS_DOC)
                .define(SslConfigs.SSL_PROTOCOL_CONFIG, ConfigDef.Type.STRING, SslConfigs.DEFAULT_SSL_PROTOCOL, ConfigDef.Importance.MEDIUM, SslConfigs.SSL_PROTOCOL_DOC)
                .define(SslConfigs.SSL_PROVIDER_CONFIG, ConfigDef.Type.STRING, null, ConfigDef.Importance.MEDIUM, SslConfigs.SSL_PROVIDER_DOC)
                .define(SslConfigs.SSL_CIPHER_SUITES_CONFIG, ConfigDef.Type.LIST, null, ConfigDef.Importance.LOW, SslConfigs.SSL_CIPHER_SUITES_DOC)
                .define(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG, ConfigDef.Type.LIST, SslConfigs.DEFAULT_SSL_ENABLED_PROTOCOLS, ConfigDef.Importance.MEDIUM, SslConfigs.SSL_ENABLED_PROTOCOLS_DOC)
                .define(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, ConfigDef.Type.STRING, SslConfigs.DEFAULT_SSL_KEYSTORE_TYPE, ConfigDef.Importance.MEDIUM, SslConfigs.SSL_KEYSTORE_TYPE_DOC)
                .define(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, ConfigDef.Type.STRING, null, ConfigDef.Importance.HIGH, SslConfigs.SSL_KEYSTORE_LOCATION_DOC)
                .define(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, ConfigDef.Type.STRING, null, ConfigDef.Importance.HIGH, SslConfigs.SSL_KEYSTORE_PASSWORD_DOC)
                .define(SslConfigs.SSL_KEY_PASSWORD_CONFIG, ConfigDef.Type.STRING, null, ConfigDef.Importance.HIGH, SslConfigs.SSL_KEY_PASSWORD_DOC)
                .define(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, ConfigDef.Type.STRING, SslConfigs.DEFAULT_SSL_TRUSTSTORE_TYPE, ConfigDef.Importance.MEDIUM, SslConfigs.SSL_TRUSTSTORE_TYPE_DOC)
                .define(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, ConfigDef.Type.STRING, null, ConfigDef.Importance.HIGH, SslConfigs.SSL_TRUSTSTORE_LOCATION_DOC)
                .define(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, ConfigDef.Type.STRING, null, ConfigDef.Importance.HIGH, SslConfigs.SSL_TRUSTSTORE_PASSWORD_DOC)
                .define(SslConfigs.SSL_KEYMANAGER_ALGORITHM_CONFIG, ConfigDef.Type.STRING, SslConfigs.DEFAULT_SSL_KEYMANGER_ALGORITHM, ConfigDef.Importance.LOW, SslConfigs.SSL_KEYMANAGER_ALGORITHM_DOC)
                .define(SslConfigs.SSL_TRUSTMANAGER_ALGORITHM_CONFIG, ConfigDef.Type.STRING, SslConfigs.DEFAULT_SSL_TRUSTMANAGER_ALGORITHM, ConfigDef.Importance.LOW, SslConfigs.SSL_TRUSTMANAGER_ALGORITHM_DOC)
                .define(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, ConfigDef.Type.STRING, null,  ConfigDef.Importance.LOW, SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_DOC)
                .define(SaslConfigs.SASL_KERBEROS_SERVICE_NAME, ConfigDef.Type.STRING, null, ConfigDef.Importance.MEDIUM, SaslConfigs.SASL_KERBEROS_SERVICE_NAME_DOC)
                .define(SaslConfigs.SASL_KERBEROS_KINIT_CMD, ConfigDef.Type.STRING, SaslConfigs.DEFAULT_KERBEROS_KINIT_CMD, ConfigDef.Importance.LOW, SaslConfigs.SASL_KERBEROS_KINIT_CMD_DOC)
                .define(SaslConfigs.SASL_KERBEROS_TICKET_RENEW_WINDOW_FACTOR, ConfigDef.Type.DOUBLE, SaslConfigs.DEFAULT_KERBEROS_TICKET_RENEW_WINDOW_FACTOR, ConfigDef.Importance.LOW, SaslConfigs.SASL_KERBEROS_TICKET_RENEW_WINDOW_FACTOR_DOC)
                .define(SaslConfigs.SASL_KERBEROS_TICKET_RENEW_JITTER, ConfigDef.Type.DOUBLE, SaslConfigs.DEFAULT_KERBEROS_TICKET_RENEW_JITTER, ConfigDef.Importance.LOW, SaslConfigs.SASL_KERBEROS_TICKET_RENEW_JITTER_DOC)
                .define(SaslConfigs.SASL_KERBEROS_MIN_TIME_BEFORE_RELOGIN, ConfigDef.Type.LONG, SaslConfigs.DEFAULT_KERBEROS_MIN_TIME_BEFORE_RELOGIN, ConfigDef.Importance.LOW, SaslConfigs.SASL_KERBEROS_MIN_TIME_BEFORE_RELOGIN_DOC)
                .define(SaslConfigs.SASL_KERBEROS_PRINCIPAL_TO_LOCAL_RULES, ConfigDef.Type.LIST, SaslConfigs.DEFAULT_SASL_KERBEROS_PRINCIPAL_TO_LOCAL_RULES, ConfigDef.Importance.MEDIUM, SaslConfigs.SASL_KERBEROS_PRINCIPAL_TO_LOCAL_RULES_DOC)
                .define(CommonClientConfigs.REQUEST_TIMEOUT_MS_CONFIG,
                        ConfigDef.Type.INT,
                        40 * 1000,
                        atLeast(0),
                        ConfigDef.Importance.MEDIUM,
                        CommonClientConfigs.REQUEST_TIMEOUT_MS_DOC)
                        /* default is set to be a bit lower than the server default (10 min), to avoid both client and server closing connection at same time */
                .define(CommonClientConfigs.CONNECTIONS_MAX_IDLE_MS_CONFIG,
                        ConfigDef.Type.LONG,
                        9 * 60 * 1000,
                        ConfigDef.Importance.MEDIUM,
                        CommonClientConfigs.CONNECTIONS_MAX_IDLE_MS_DOC)
                .define(WORKER_SYNC_TIMEOUT_MS_CONFIG,
                        ConfigDef.Type.INT,
                        3000,
                        ConfigDef.Importance.MEDIUM,
                        WORKER_SYNC_TIMEOUT_MS_DOC)
                .define(WORKER_UNSYNC_BACKOFF_MS_CONFIG,
                        ConfigDef.Type.INT,
                        WORKER_UNSYNC_BACKOFF_MS_DEFAULT,
                        ConfigDef.Importance.MEDIUM,
                        WORKER_UNSYNC_BACKOFF_MS_DOC);
    }

    public DistributedConfig(Map<String, String> props) {
        super(CONFIG, props);
    }

}
