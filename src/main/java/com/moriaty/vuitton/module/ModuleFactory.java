package com.moriaty.vuitton.module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 模块工厂
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 上午11:34
 */
public class ModuleFactory {

    private ModuleFactory() {

    }

    private static List<Module> factoryModuleList = new ArrayList<>();

    public static void addModule(Module module) {
        factoryModuleList.add(module);
        sort();
    }

    public static List<Module> getAllModule() {
        return factoryModuleList;
    }

    private static void sort() {
        factoryModuleList = factoryModuleList.stream().sorted(Comparator.comparing(Module::getId))
                .collect(Collectors.toList());
    }
}
