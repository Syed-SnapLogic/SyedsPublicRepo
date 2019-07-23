/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syed.jrubytest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gaian
 */
public class Test2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Test2.class);
    private static final String HOOK = "hook";
    private static final String INPUT = "input";
    private static final String OUTPUT = "output";
    private static final String ERROR = "error";
    private static final String LOG = "log";

    public static void main(String str[]) throws Exception {
        String scriptContent = "class MyScript\n"
                + "    # Import the interface required by the Script snap.\n"
                + "    include com.syed.jrubytest.ScriptHook\n"
                + "    attr_reader :log, :input, :output, :error\n"
                + "    def initialize(log, input, output, error)\n"
                + "        puts input.class.name\n"
                + "        @input = input\n"
                + "        @output = output\n"
                + "    end\n"
                + "\n"
                + "    # The \"execute()\" method is called once when the pipeline is started\n"
                + "    # and allowed to process its inputs or just send data to its outputs.\n"
                + "    def execute()\n"
                + "        while input.hasNext() do\n"
                + "            begin\n"
                + "                # Read the next document, wrap it in a map and write out the wrapper\n"
                + "                doc = input.next()\n"
                + "                output.write(doc)\n"
                + "            \n"
                + "            rescue => e\n"
                + "                puts \"Bad Rublet \" + e.message\n"
                + "            end\n"
                + "        end\n"
                + "    end\n"
                + "end\n"
                + "\n"
                + "# The Script Snap will look for a ScriptHook object in the \"hook\"\n"
                + "# variable.  The snap will then call the hook's \"execute\" method.\n"
                + "hook = MyScript.new($log, $input, $output, $error)";
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("jruby");
        Bindings bindings = engine.createBindings();
        engine.eval(scriptContent, bindings);

        List<Map> maps = new ArrayList<>();
        Map<String, String> m1 = new LinkedHashMap<>();
        m1.put("name", "syed");
        maps.add(m1);

        ScriptHookInput input = new ScriptHookInput(maps.iterator());
        ScriptHook.Output output = new ScriptHookOutput();
        ScriptHook.Output error = new ScriptHookOutput();

        bindings.put(INPUT, input);
        bindings.put(OUTPUT, output);
        bindings.put(ERROR, error);
        bindings.put(LOG, LOGGER);
        engine.eval(scriptContent, bindings);

        Object hook = bindings.get(HOOK);
        if (hook == null) {
            hook = bindings.get("$" + HOOK);
        }
        if (!(hook instanceof ScriptHook)) {
            throw new RuntimeException("invalid hook object!!");
        }
        ((ScriptHook) hook).execute();
    }
}
