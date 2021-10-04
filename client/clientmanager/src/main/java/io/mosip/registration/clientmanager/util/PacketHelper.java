package io.mosip.registration.clientmanager.util;

import io.mosip.registration.clientmanager.dto.packet.ProviderDto;

import java.util.*;
import java.util.stream.Collectors;

public class PacketHelper {

    private static String SOURCE = "source";
    private static String PROCESS = "process";
    private static String CLASSNAME = "classname";

    private static List<ProviderDto> readerProvider = null;

    private static List<ProviderDto> writerProvider = null;

    public enum Provider {
        READER, WRITER;
    }

    /**
     * The providerConfig.
     */
    private static Map<String, String> readerConfiguration;
    /**
     * The providerConfig.
     */
    private static Map<String, String> writerConfiguration;

    public static Set<String> getReaderProvider(Map<String, String> readerConfig) {
        readerConfiguration = readerConfig;
        Set<String> readerProvider = null;
        List<ProviderDto> providerDtos = getReader(readerConfig);
        if (providerDtos != null && !providerDtos.isEmpty()) {
            readerProvider = providerDtos.stream().map(p -> p.className).collect(Collectors.toSet());
        }
        return readerProvider;
    }

    public static Set<String> getWriterProvider(Map<String, String> writerConfig) {
        writerConfiguration = writerConfig;
        Set<String> writerProvider = null;
        List<ProviderDto> providerDtos = getWriter(writerConfig);
        if (providerDtos != null && !providerDtos.isEmpty()) {
            writerProvider = providerDtos.stream().map(p -> p.className).collect(Collectors.toSet());
        }
        return writerProvider;
    }

    public static boolean isSourceAndProcessPresent(String providerName, String providerSource, String providerProcess, Provider providerEnum) {
        List<ProviderDto> configurations = null;

        if (Provider.READER.equals(providerEnum))
            configurations = getReader(readerConfiguration);
        else if (Provider.WRITER.equals(providerEnum))
            configurations = getWriter(writerConfiguration);

        if (configurations == null)
            throw new RuntimeException("No Available Provider Exception");
            //throw new NoAvailableProviderException();

        Optional<ProviderDto> providerDto = configurations.stream().filter(dto -> dto.source.toUpperCase().contains(providerSource.toUpperCase())
                && dto.process.toUpperCase().contains(providerProcess.toUpperCase())).findAny();
        return providerDto.isPresent() && providerDto.get() != null && providerName.contains(providerDto.get().className);
    }

    private static List<ProviderDto> getReader(Map<String, String> readerConfiguration) {
        if (readerProvider == null) {
            List<ProviderDto> providerDtos = new ArrayList<>();
            if (readerConfiguration != null && !readerConfiguration.isEmpty()) {
                for (String value : readerConfiguration.values()) {
                    String[] values = value.split(",");
                    ProviderDto providerDto = new ProviderDto();
                    for (String provider : values) {
                        if (provider != null) {
                            if (provider.startsWith(SOURCE))
                                providerDto.source = (provider.replace(SOURCE + ":", ""));
                            else if (provider.startsWith(PROCESS))
                                providerDto.process = (provider.replace(PROCESS + ":", ""));
                            else if (provider.startsWith(CLASSNAME))
                                providerDto.className = (provider.replace(CLASSNAME + ":", ""));
                        }
                    }
                    providerDtos.add(providerDto);
                }
            }
            readerProvider = providerDtos;
        }
        return readerProvider;
    }

    private static List<ProviderDto> getWriter(Map<String, String> writerConfiguration) {
        if (writerProvider == null) {
            List<ProviderDto> providerDtos = new ArrayList<>();
            if (writerConfiguration != null && !writerConfiguration.isEmpty()) {
                for (String value : writerConfiguration.values()) {
                    String[] values = value.split(",");
                    ProviderDto providerDto = new ProviderDto();
                    for (String provider : values) {
                        if (provider != null) {
                            if (provider.startsWith(SOURCE))
                                providerDto.source = (provider.replace(SOURCE + ":", ""));
                            else if (provider.startsWith(PROCESS))
                                providerDto.process = (provider.replace(PROCESS + ":", ""));
                            else if (provider.startsWith(CLASSNAME))
                                providerDto.className = (provider.replace(CLASSNAME + ":", ""));
                        }
                    }
                    providerDtos.add(providerDto);
                }
            }
            writerProvider = providerDtos;
        }
        return writerProvider;
    }
}
