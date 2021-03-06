/*
 *  The MIT License
 *
 *  Copyright 2011 Sony Ericsson Mobile Communications. All rights reserved.
 *  Copyright 2012 Sony Mobile Communications AB. All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger;

import com.sonyericsson.hudson.plugins.gerrit.gerritevents.dto.attr.Account;
import com.sonyericsson.hudson.plugins.gerrit.gerritevents.dto.attr.Provider;
import com.sonyericsson.hudson.plugins.gerrit.gerritevents.dto.events.ChangeAbandoned;
import com.sonyericsson.hudson.plugins.gerrit.gerritevents.dto.events.ChangeBasedEvent;
import com.sonyericsson.hudson.plugins.gerrit.gerritevents.dto.events.ChangeRestored;
import com.sonyericsson.hudson.plugins.gerrit.gerritevents.dto.events.GerritTriggeredEvent;
import com.sonyericsson.hudson.plugins.gerrit.gerritevents.dto.events.RefUpdated;
import com.sonyericsson.hudson.plugins.gerrit.trigger.GerritServer;
import com.sonyericsson.hudson.plugins.gerrit.trigger.PluginImpl;
import com.sonyericsson.hudson.plugins.gerrit.trigger.config.IGerritHudsonTriggerConfig;
import com.sonyericsson.hudson.plugins.gerrit.trigger.utils.StringUtil;

import hudson.model.AbstractProject;
import hudson.model.ParameterValue;
import hudson.model.StringParameterValue;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The parameters to add to a build.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public enum GerritTriggerParameters {
    /**
     * Parameter name for the commit subject (commit message's 1st line).
     */
    GERRIT_CHANGE_SUBJECT,
    /**
     * Parameter name for the full commit message.
     */
    GERRIT_CHANGE_COMMIT_MESSAGE,
    /**
     * Parameter name for the branch.
     */
    GERRIT_BRANCH,
    /**
     * Parameter name for the topic.
     */
    GERRIT_TOPIC,
    /**
     * Parameter name for the change-id.
     */
    GERRIT_CHANGE_ID,
    /**
     * Parameter name for the change number.
     */
    GERRIT_CHANGE_NUMBER,
    /**
     * Parameter name for the URL to the change.
     */
    GERRIT_CHANGE_URL,
    /**
     * Parameter name for the patch set number.
     */
    GERRIT_PATCHSET_NUMBER,
    /**
     * Parameter name for the patch set revision.
     */
    GERRIT_PATCHSET_REVISION,
    /**
     * Parameter name for the Gerrit project name.
     */
    GERRIT_PROJECT,
    /**
     * Parameter name for the refspec.
     */
    GERRIT_REFSPEC,
    /**
     * The name and email of the abandoner of the change.
     */
    GERRIT_CHANGE_ABANDONER,
    /**
     * The name of the abandoner of the change.
     */
    GERRIT_CHANGE_ABANDONER_NAME,
    /**
     * The email of the abandoner of the change.
     */
    GERRIT_CHANGE_ABANDONER_EMAIL,
    /**
     * The name and email of the owner of the change.
     */
    GERRIT_CHANGE_OWNER,
    /**
     * The name of the owner of the change.
     */
    GERRIT_CHANGE_OWNER_NAME,
    /**
     * The email of the owner of the change.
     */
    GERRIT_CHANGE_OWNER_EMAIL,
    /**
     * The name and email of the restorer of the change.
     */
    GERRIT_CHANGE_RESTORER,
    /**
     * The name of the restorer of the change.
     */
    GERRIT_CHANGE_RESTORER_NAME,
    /**
     * The email of the restorer of the change.
     */
    GERRIT_CHANGE_RESTORER_EMAIL,
    /**
     * The name and email of the uploader of the patch-set.
     */
    GERRIT_PATCHSET_UPLOADER,
    /**
     * The name of the uploader of the patch-set.
     */
    GERRIT_PATCHSET_UPLOADER_NAME,
    /**
     * The email of the uploader of the patch-set.
     */
    GERRIT_PATCHSET_UPLOADER_EMAIL,
    /**
     * The name and email of the person who triggered the event.
     */
    GERRIT_EVENT_ACCOUNT,
    /**
     * The name of the person who triggered the event.
     */
    GERRIT_EVENT_ACCOUNT_NAME,
    /**
     * The email of the person who triggered the event.
     */
    GERRIT_EVENT_ACCOUNT_EMAIL,
    /**
     * The refname in a ref-updated event.
     */
    GERRIT_REFNAME,
    /**
     * The old revision in a ref-updated event.
     */
    GERRIT_OLDREV,
    /**
     * The new revision in a ref-updated event.
     */
    GERRIT_NEWREV,
    /**
     * The submitter in a ref-updated event.
     */
    GERRIT_SUBMITTER,
    /**
     * The name of the submitter in a ref-updated event.
     */
    GERRIT_SUBMITTER_NAME,
    /**
     * The email of the submitter in a ref-updated event.
     */
    GERRIT_SUBMITTER_EMAIL,
    /**
     * The name of the Gerrit instance.
     */
    GERRIT_NAME,
    /**
     * The host of the Gerrit instance.
     */
    GERRIT_HOST,
    /**
     * The port number of the Gerrit instance.
     */
    GERRIT_PORT,
    /**
     * The protocol scheme of the Gerrit instance.
     */
    GERRIT_SCHEME,
    /**
     * The version of the Gerrit instance.
     */
    GERRIT_VERSION,
    /**
     * A hashcode of the Gerrit event object, to make sure every set of parameters
     * is unique (allowing jenkins to queue duplicate builds).
     */
    GERRIT_EVENT_HASH,
    /**
     * The type of the event.
     */
    GERRIT_EVENT_TYPE;

    private static final Logger logger = LoggerFactory.getLogger(GerritTriggerParameters.class);

    /**
     * Creates a {@link hudson.model.StringParameterValue} and adds it to the provided list.
     * If the parameter with the same name already exists in the list it will be replaced by the new parameter,
     * but its description will be used, unless the parameter type is something else than a StringParameterValue.
     *
     * @param parameters   the list of existing parameters.
     * @param value        the value.
     * @param escapeQuotes if quote characters should be escaped.
     */
    public void setOrCreateStringParameterValue(List<ParameterValue> parameters, String value, boolean escapeQuotes) {
        ParameterValue parameter = null;
        for (ParameterValue p : parameters) {
            if (p.getName().toUpperCase().equals(this.name())) {
                parameter = p;
                break;
            }
        }
        String description = null;
        if (parameter != null) {
            if (parameter instanceof StringParameterValue) {
                //Perhaps it is manually added to remind the user of what it is for.
                description = parameter.getDescription();
            }
            parameters.remove(parameter);
        }
        String stringValue;
        if (escapeQuotes) {
            stringValue = StringUtil.escapeQuotes(value);
        } else {
            stringValue = value;
        }
        if (stringValue == null) {
            stringValue = "";
        }

        parameter = new StringParameterValue(this.name(), stringValue, description);
        parameters.add(parameter);
    }

    /**
     * Adds or sets all the Gerrit-parameter values to the provided list.
     * @param gerritEvent the event.
     * @param parameters the default parameters
     * @see #setOrCreateStringParameterValue(java.util.List, String, boolean)
     */
    public static void setOrCreateParameters(GerritTriggeredEvent gerritEvent,
                                             List<ParameterValue> parameters) {
        setOrCreateParameters(gerritEvent, null, parameters);
    }

    /**
     * Adds or sets all the Gerrit-parameter values to the provided list.
     * @param gerritEvent the event.
     * @param project the project for which the parameters are being set
     * @param parameters the default parameters
     * @see #setOrCreateStringParameterValue(java.util.List, String, boolean)
     */
    public static void setOrCreateParameters(GerritTriggeredEvent gerritEvent, AbstractProject project,
            List<ParameterValue> parameters) {

        boolean noNameAndEmailParameters = false;
        boolean escapeQuotes = false;
        if (project != null) {
            GerritTrigger trigger = GerritTrigger.getTrigger(project);
            if (trigger != null) {
                noNameAndEmailParameters = trigger.isNoNameAndEmailParameters();
                escapeQuotes = trigger.isEscapeQuotes();
            }
        }

        GERRIT_EVENT_TYPE.setOrCreateStringParameterValue(
                parameters, gerritEvent.getEventType().getTypeValue(), escapeQuotes);
        GERRIT_EVENT_HASH.setOrCreateStringParameterValue(
                parameters, String.valueOf(((java.lang.Object)gerritEvent).hashCode()), escapeQuotes);
        if (gerritEvent instanceof ChangeBasedEvent) {
            ChangeBasedEvent event = (ChangeBasedEvent)gerritEvent;
            GERRIT_BRANCH.setOrCreateStringParameterValue(
                    parameters, event.getChange().getBranch(), escapeQuotes);
            GERRIT_TOPIC.setOrCreateStringParameterValue(
                    parameters, event.getChange().getTopic(), escapeQuotes);
            GERRIT_CHANGE_NUMBER.setOrCreateStringParameterValue(
                    parameters, event.getChange().getNumber(), escapeQuotes);
            GERRIT_CHANGE_ID.setOrCreateStringParameterValue(
                    parameters, event.getChange().getId(), escapeQuotes);
            String pNumber = null;
            if (null != event.getPatchSet()) {
                pNumber = event.getPatchSet().getNumber();
                GERRIT_PATCHSET_NUMBER.setOrCreateStringParameterValue(
                        parameters, pNumber, escapeQuotes);
                GERRIT_PATCHSET_REVISION.setOrCreateStringParameterValue(
                        parameters, event.getPatchSet().getRevision(), escapeQuotes);
                GERRIT_REFSPEC.setOrCreateStringParameterValue(
                        parameters, StringUtil.makeRefSpec(event), escapeQuotes);
            }
            GERRIT_PROJECT.setOrCreateStringParameterValue(
                    parameters, event.getChange().getProject(), escapeQuotes);
            if (event instanceof ChangeRestored) {
                if (!noNameAndEmailParameters) {
                    GERRIT_CHANGE_RESTORER.setOrCreateStringParameterValue(
                            parameters, getNameAndEmail(((ChangeRestored)event).getRestorer()), escapeQuotes);
                }
                GERRIT_CHANGE_RESTORER_NAME.setOrCreateStringParameterValue(
                        parameters, getName(((ChangeRestored)event).getRestorer()), escapeQuotes);
                GERRIT_CHANGE_RESTORER_EMAIL.setOrCreateStringParameterValue(
                        parameters, getEmail(((ChangeRestored)event).getRestorer()), escapeQuotes);
            }
            GERRIT_CHANGE_SUBJECT.setOrCreateStringParameterValue(
                    parameters, event.getChange().getSubject(), escapeQuotes);

            String url = getURL(event, project);

            String commitMessage = event.getChange().getCommitMessage();
            if (commitMessage != null) {
                try {
                    byte[] encodedBytes = Base64.encodeBase64(commitMessage.getBytes("UTF-8"));
                    GERRIT_CHANGE_COMMIT_MESSAGE.setOrCreateStringParameterValue(
                        parameters, new String(encodedBytes), escapeQuotes);
                } catch (UnsupportedEncodingException uee) {
                    logger.error("Failed to encode commit message as Base64: ", uee);
                }
            }
            GERRIT_CHANGE_URL.setOrCreateStringParameterValue(
                    parameters, url, escapeQuotes);
            if (event instanceof ChangeAbandoned) {
                if (!noNameAndEmailParameters) {
                    GERRIT_CHANGE_ABANDONER.setOrCreateStringParameterValue(
                            parameters, getNameAndEmail(((ChangeAbandoned)event).getAbandoner()), escapeQuotes);
                }
                GERRIT_CHANGE_ABANDONER_NAME.setOrCreateStringParameterValue(
                        parameters, getName(((ChangeAbandoned)event).getAbandoner()), escapeQuotes);
                GERRIT_CHANGE_ABANDONER_EMAIL.setOrCreateStringParameterValue(
                        parameters, getEmail(((ChangeAbandoned)event).getAbandoner()), escapeQuotes);
            }
            if (!noNameAndEmailParameters) {
                GERRIT_CHANGE_OWNER.setOrCreateStringParameterValue(
                    parameters, getNameAndEmail(event.getChange().getOwner()), escapeQuotes);
            }
            GERRIT_CHANGE_OWNER_NAME.setOrCreateStringParameterValue(
                    parameters, getName(event.getChange().getOwner()), escapeQuotes);
            GERRIT_CHANGE_OWNER_EMAIL.setOrCreateStringParameterValue(
                    parameters, getEmail(event.getChange().getOwner()), escapeQuotes);
            Account uploader = findUploader(event);
            if (!noNameAndEmailParameters) {
                GERRIT_PATCHSET_UPLOADER.setOrCreateStringParameterValue(
                    parameters, getNameAndEmail(uploader), escapeQuotes);
            }
            GERRIT_PATCHSET_UPLOADER_NAME.setOrCreateStringParameterValue(
                    parameters, getName(uploader), escapeQuotes);
            GERRIT_PATCHSET_UPLOADER_EMAIL.setOrCreateStringParameterValue(
                    parameters, getEmail(uploader), escapeQuotes);
        } else if (gerritEvent instanceof RefUpdated) {
            RefUpdated event = (RefUpdated)gerritEvent;
            GERRIT_REFNAME.setOrCreateStringParameterValue(
                    parameters, event.getRefUpdate().getRefName(), escapeQuotes);
            GERRIT_PROJECT.setOrCreateStringParameterValue(
                    parameters, event.getRefUpdate().getProject(), escapeQuotes);
            GERRIT_OLDREV.setOrCreateStringParameterValue(
                    parameters, event.getRefUpdate().getOldRev(), escapeQuotes);
            GERRIT_NEWREV.setOrCreateStringParameterValue(
                    parameters, event.getRefUpdate().getNewRev(), escapeQuotes);
        }
        Account account = gerritEvent.getAccount();
        if (account != null) {
            if (!noNameAndEmailParameters) {
                GERRIT_EVENT_ACCOUNT.setOrCreateStringParameterValue(
                        parameters, getNameAndEmail(account), escapeQuotes);
            }
            GERRIT_EVENT_ACCOUNT_NAME.setOrCreateStringParameterValue(
                    parameters, getName(account), escapeQuotes);
            GERRIT_EVENT_ACCOUNT_EMAIL.setOrCreateStringParameterValue(
                    parameters, getEmail(account), escapeQuotes);
        }
        Provider provider = gerritEvent.getProvider();
        if (provider != null) {
            GERRIT_NAME.setOrCreateStringParameterValue(
                    parameters, provider.getName(), escapeQuotes);
            GERRIT_HOST.setOrCreateStringParameterValue(
                    parameters, provider.getHost(), escapeQuotes);
            GERRIT_PORT.setOrCreateStringParameterValue(
                    parameters, provider.getPort(), escapeQuotes);
            GERRIT_SCHEME.setOrCreateStringParameterValue(
                    parameters, provider.getScheme(), escapeQuotes);
            GERRIT_VERSION.setOrCreateStringParameterValue(
                    parameters, provider.getVersion(), escapeQuotes);
        }
    }

    /**
     * Get the front end url from a ChangeBasedEvent.
     *
     * @param event the event
     * @param project the project for which the parameters are being set
     * @return the front end url
     */
    private static String getURL(ChangeBasedEvent event, AbstractProject project) {
        String url = "";
        String serverName = null;
        //Figure out what serverName to use
        if (event.getProvider() != null) {
            serverName = event.getProvider().getName();
        } else if (project != null) {
            String name = GerritTrigger.getTrigger(project).getServerName();
            if (!GerritServer.ANY_SERVER.equals(name)) {
                serverName = name;
            }
        }
        if (serverName == null && PluginImpl.getInstance().getFirstServer() != null) {
            logger.warn("No server could be determined from event or project config, "
                    + "defaulting to the first configured server. Event: [{}] Project: [{}]", event, project);
            serverName = PluginImpl.getInstance().getFirstServer().getName();
        } else if (serverName == null) {
            //We have exhausted all possibilities, time to fail horribly
            throw new IllegalStateException("Cannot determine a Gerrit server to link to. Have you configured one?");
        }

        GerritServer server = PluginImpl.getInstance().getServer(serverName);
        if (server != null) {
            IGerritHudsonTriggerConfig config = server.getConfig();
            if (config != null) {
                url = config.getGerritFrontEndUrlFor(event);
            } else {
                logger.error("Could not find config for Gerrit server {}", serverName);
            }
        } else {
            logger.error("Could not find Gerrit server {}", serverName);
        }
        return url;
    }

    /**
     * There are two uploader fields in the event, this method gets one of them if one is null.
     *
     * @param event the event to search.
     * @return the uploader if any.
     */
    private static Account findUploader(ChangeBasedEvent event) {
        if (event.getPatchSet() != null && event.getPatchSet().getUploader() != null) {
            return event.getPatchSet().getUploader();
        } else {
            return event.getAccount();
        }
    }

    /**
     * Convenience method to avoid NPE on none existent accounts.
     *
     * @param account the account.
     * @return the name in the account or null if Account is null.
     * @see com.sonyericsson.hudson.plugins.gerrit.gerritevents.dto.attr.Account#getName()
     */
    private static String getName(Account account) {
        if (account == null) {
            return "";
        } else {
            return account.getName();
        }
    }

    /**
     * Convenience method to avoid NPE on none existent accounts.
     *
     * @param account the account.
     * @return the name and email in the account or null if Account is null.
     * @see com.sonyericsson.hudson.plugins.gerrit.gerritevents.dto.attr.Account#getNameAndEmail()
     */
    private static String getNameAndEmail(Account account) {
        if (account == null) {
            return "";
        } else {
            return account.getNameAndEmail();
        }
    }

    /**
     * Convenience method to avoid NPE on none existent accounts.
     *
     * @param account the account.
     * @return the email in the account or null if Account is null.
     * @see com.sonyericsson.hudson.plugins.gerrit.gerritevents.dto.attr.Account#getEmail()
     */
    private static String getEmail(Account account) {
        if (account == null) {
            return "";
        } else {
            return account.getEmail();
        }
    }
}
