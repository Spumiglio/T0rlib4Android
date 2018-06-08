/*
Copyright (C) 2011-2014 Sublime Software Ltd

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

THIS CODE IS PROVIDED ON AN *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR IMPLIED,
INCLUDING WITHOUT LIMITATION ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A PARTICULAR PURPOSE,
MERCHANTABLITY OR NON-INFRINGEMENT.

See the Apache 2 License for the specific language governing permissions and limitations under the License.
*/

package net.sf.msopentech.thali.java.toronionproxy;

import net.sf.controller.network.NetLayerStatus;
import net.sf.controller.network.ServiceDescriptor;
import net.sf.freehaven.tor.control.EventHandler;



import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Logs the data we get from notifications from the Tor OP. This is really just meant for debugging.
 */
public class OnionProxyManagerEventHandler implements EventHandler {

    private ServiceDescriptor hs;
    private NetLayerStatus listener;
    private boolean hsPublished;

    public void setHStoWatchFor(ServiceDescriptor hs, NetLayerStatus listener) {
        if (hs == this.hs && hsPublished) {
            listener.onConnect(hs);
            return;
        }
        this.listener = listener;
        this.hs = hs;
        hsPublished = false;
    }

    public void circuitStatus(String status, String id, List<String> path, Map<String, String> info) {
        String msg = "CircuitStatus: " + id + " " + status;
        String purpose = info.get("PURPOSE");
        if (purpose != null) msg += ", purpose: " + purpose;
        String hsState = info.get("HS_STATE");
        if (hsState != null) msg += ", state: " + hsState;
        String rendQuery = info.get("REND_QUERY");
        if (rendQuery != null) msg += ", service: " + rendQuery;
        if (!path.isEmpty()) msg += ", path: " + shortenPath(path);
        System.out.println(msg);
    }

    @Override
    public void circuitStatus(String status, String circID, String path) {
        System.out.println("streamStatus: status: " + status + ", circID: " + circID + ", path: " + path);
    }

    public void streamStatus(String status, String id, String target) {
        System.out.println("streamStatus: status: " + status + ", id: " + id + ", target: " + target);
    }

    public void orConnStatus(String status, String orName) {
        System.out.println("OR connection: status: " + status + ", orName: " + orName);
    }

    public void bandwidthUsed(long read, long written) {
        System.out.println("bandwidthUsed: read: " + read + ", written: " + written);
    }

    public void newDescriptors(List<String> orList) {
        Iterator<String> iterator = orList.iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next());
        }
        System.out.println(  stringBuilder.toString());
    }

    //fetch Exit Node
    public void message(String severity, String msg) {
        System.out.println("message: severity: " + severity + ", msg: " + msg);
    }

    public void unrecognized(String type, String msg) {
        System.out.println("unrecognized: type: " + type + ", msg: " + msg);
    }

    private String shortenPath(List<String> path) {
        StringBuilder s = new StringBuilder();
        for (String id : path) {
            if (s.length() > 0) s.append(',');
            s.append(id.substring(1, 7));
        }
        return s.toString();
    }

}
