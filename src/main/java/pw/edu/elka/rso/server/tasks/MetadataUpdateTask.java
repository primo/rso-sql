package pw.edu.elka.rso.server.tasks;

import pw.edu.elka.rso.server.Task;
import pw.edu.elka.rso.logic.QueryExecution.*;

public class MetadataUpdateTask extends Task<MetadataUpdatePack> {
    public MetadataUpdateTask(MetadataUpdatePack metadata_update_pack){
        input = metadata_update_pack;
    }
}
