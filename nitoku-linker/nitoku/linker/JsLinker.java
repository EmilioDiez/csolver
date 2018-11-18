/**
 *  Javascript Constraint Solver (CSolver) Copyright (c) 2016, 
 *  Emilio Diez
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package nitoku.linker;

import java.util.Set;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.ConfigurationProperty;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.SelectionProperty;
import com.google.gwt.dev.About;
import com.google.gwt.dev.util.DefaultTextOutput;

@LinkerOrder(LinkerOrder.Order.PRIMARY)
public class JsLinker extends AbstractLinker {

    @Override
    public String getDescription() {
        return "Nitoku Linker - generate js for html5 web worker";
    }

    public ArtifactSet link(TreeLogger logger, LinkerContext context, ArtifactSet artifacts)
                    throws UnableToCompleteException {

        ArtifactSet toReturn = new ArtifactSet(artifacts);
        DefaultTextOutput out = new DefaultTextOutput(true);
        String userAgentName = getUserAgentName(logger, context);
        
        //long compilationTime = System.currentTimeMillis();
        out.print("(function(){");
        out.newline();

        //String userAgentName = getUserAgentName(logger, context);
        String s = getModuleName(logger, context);

        
        // get compilation result
        Set<CompilationResult> results = artifacts.find(CompilationResult.class);
        if (results.size() == 0) {
            logger.log(TreeLogger.WARN, "Requested 0 permutations");
            return toReturn;
        }
        
        
        CompilationResult result = results.iterator().next();

        // get the generated javascript
        String[] javaScript = result.getJavaScript();

        out.newline();
        
        ////out.print("var $wnd;");
        //out.newline();
        //out.print("var $doc;");
        
        //out.print("var $wnd = {Error:Error , JSON: JSON, setTimeout:setTimeout , Math:Math , location:location };");
        //out.print("var $doc = {compatMode : 'BackCompat'};");
        
        //out.print("var $workergwtbridge;"); // gwtwwlinker - mind the prototype here
        out.print("var $moduleName, $moduleBase;");
        out.newline();
        //out.print("if(typeof(window) != 'undefined'){ $wnd = window;  $doc = $wnd.document; }");
        //out.newline();
        out.print("window = this;");
        // Alias self as well since it is a magic var sometimes provided
        // by the webworker linker.
        out.newline();
        out.print("self = window;");
        out.newline();
        out.print("$wnd = window;  $doc = $wnd.document;");
        
        out.newline();        
        //out.print("else{ $wnd = {Error:Error , JSON: JSON, setTimeout:setTimeout , Math:Math }; }"); // gwtwwlinker - mind the $wnd.JSON passthrough used by autobeans
        out.newline();
        out.print("var $gwt_version = \"" + About.getGwtVersionNum() + "\";");
        
        out.print("var location = {search : ''};");

        //the script should not be using documentMode, only ie uses it
        out.print("var $doc = {compatMode : 'CSS1Compat', documentMode: '9'};");

        out.print("var $stats = function(){};");
        out.newline();
        out.print("var $sessionId = function(){};");
        out.newline();
        out.print("var navigator = {}; navigator.userAgent = {};" +
    			"navigator.userAgent.toLowerCase = function() {	return \""+userAgentName+"\";};");
        
        out.newline();
        
        out.newlineOpt();
        out.print(javaScript[0]);
        out.newline();

        
        out.newline();
        out.print("$strongName = '" + result.getStrongName() + "';");
        out.newline();

        out.print("gwtOnLoad(null,'" + context.getModuleName() + "',null);");
        out.newline();
        
        out.print("var "+s + " = self." + s + ";");
        out.newline();
        
        //worker hooks
        //out.print("onmessage = function(e) { " + 
        //          "console.log('Message received from main script');"+ 
        //          "var workerResult = 'Result: ' + e.data;"+
        //		  "console.log('Posting message back to main script');"+
        //          "postMessage(workerResult); }");

        //out.print("self.addEventListener('message', function(e) {   "+ s +".WorkerMng.workerBridge(e.data); }, false);");
        
        out.print("})();");
        out.newline();

       
        //String filename = context.getModuleName() + result.getStrongName()+ ".cache.js";
        
        //logger.log(TreeLogger.INFO,"[USER_AGENT]  "+System.getProperty("user.agent"));
        //logger.log(TreeLogger.INFO,"[GWT_NAME]  "+About.getGwtName());
        //logger.log(TreeLogger.INFO,"[GWT_NAME]  "+About.getGwtVersion());
        //for(SelectionProperty sp : context.getProperties()){
        //	logger.log(TreeLogger.INFO,"[PROPERTIES_NAME:]  "  +sp.getName());
        //	logger.log(TreeLogger.INFO,"[PROPERTIES_NAME:]  "  +sp.tryGetValue());
        //}
        //logger.log(TreeLogger.INFO,"[PROPERTIES]  "+context.getProperties().toString());
        //for(ConfigurationProperty cp : context.getConfigurationProperties()){
        //	logger.log(TreeLogger.INFO,"[CONFIGURATION_PROPERTIES_NAME:]  "  +cp.getName());
        //	logger.log(TreeLogger.INFO,"[CONFIGURATION_PROPERTIES_VALUE]  "  +cp.getValues().toString());
        //}
         
        
        //String filename = s+ "."+userAgentName+"."+getVersionName(logger, context)+".js";
        String filename = s+ "."+getVersionName(logger, context)+".js";
        
        logger.log(TreeLogger.INFO,"[CREATED file :]  "  +filename);
        
        toReturn.add(emitString(logger, out.toString(), filename));

        return toReturn;
    }


	private String getVersionName(TreeLogger logger, LinkerContext context) {
        for(ConfigurationProperty cp : context.getConfigurationProperties()){
        	if(cp.getName().equals("nitoku.linker.version")){
        		return cp.getValues().get(0);
        	}
        }
        
        logger.log(TreeLogger.ERROR, "could not find the nitoku.linker.version property");
        
        for(ConfigurationProperty cp : context.getConfigurationProperties()){
        	logger.log(TreeLogger.INFO,"[CONFIGURATION_PROPERTIES_NAME:]  "  +cp.getName());
        	logger.log(TreeLogger.INFO,"[CONFIGURATION_PROPERTIES_VALUE]  "  +cp.getValues().toString());
        }
        
		return null;
	}

	private String getModuleName(TreeLogger logger, LinkerContext context) {
        for(ConfigurationProperty cp : context.getConfigurationProperties()){
        	if(cp.getName().equals("nitoku.linker.file.name")){
        		return cp.getValues().get(0);
        	}
        }
        
        logger.log(TreeLogger.ERROR, "could not find the nitoku.linker.file.name property");
        
        for(ConfigurationProperty cp : context.getConfigurationProperties()){
        	logger.log(TreeLogger.INFO,"[CONFIGURATION_PROPERTIES_NAME:]  "  +cp.getName());
        	logger.log(TreeLogger.INFO,"[CONFIGURATION_PROPERTIES_VALUE]  "  +cp.getValues().toString());
        }
        
		return null;
	}

	private String getUserAgentName(TreeLogger logger, LinkerContext context) {
        for(SelectionProperty sp : context.getProperties()){
        	if(sp.getName().equals("user.agent")){
        		return sp.tryGetValue();
        	}
        }
        
        logger.log(TreeLogger.ERROR, "could not find the user agent value");
        
        for(SelectionProperty sp : context.getProperties()){
        	logger.log(TreeLogger.INFO,"[PROPERTIES_NAME:]  "  +sp.getName());
        	logger.log(TreeLogger.INFO,"[PROPERTIES_NAME:]  "  +sp.tryGetValue());
        }
        
		return null;
	}

}
