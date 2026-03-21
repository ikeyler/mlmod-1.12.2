package ikeyler.mlmod.variables;

import ikeyler.mlmod.Main;
import ikeyler.mlmod.util.ModUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static ikeyler.mlmod.util.ModUtils.VAR_SEPARATOR;

public class VarCollector {
    private final File dataFile = new File("mlmodVars.txt");

    public VarCollector() {
        try {
            if (dataFile.createNewFile()) Main.logger.info("created varcollector data file: {}", dataFile.getName());
        }
        catch (IOException e) {
            Main.logger.error("could not create varcollector data file:", e);
        }
    }

    public void addVariable(Variable variable) {
        // varType::varName::varNbt
        String line = variable.getType().name() +
                VAR_SEPARATOR +
                variable.getName() +
                VAR_SEPARATOR +
                variable.getNbt();
        writeLine(line);
    }

    public boolean removeVariable(Variable variable) {
        try {
            List<String> lines = ModUtils.readAllLines(dataFile);
            boolean contains = lines.removeIf(line -> line.contains(
                    variable.getType().name() + VAR_SEPARATOR + variable.getName() + VAR_SEPARATOR + variable.getNbt()));
            if (contains) {
                Files.write(Paths.get(dataFile.getPath()), lines);
                return true;
            }
        }
        catch (Exception e) {
            Main.logger.error("error while writing file:", e);
        }
        return false;
    }

    public List<Variable> readVariables() {
        return ModUtils.readAllLines(dataFile).stream().map(Variable::fromString).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private void writeLine(String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile, true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            Main.logger.error("error while writing file:", e);
        }
    }
}
