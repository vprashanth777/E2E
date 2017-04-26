package com.java;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.commons.exec.ExecuteException;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.GetCreateIssueMetadataOptionsBuilder;
import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.SearchRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.BasicPriority;
import com.atlassian.jira.rest.client.domain.BasicUser;
import com.atlassian.jira.rest.client.domain.CimIssueType;
import com.atlassian.jira.rest.client.domain.CimProject;
import com.atlassian.jira.rest.client.domain.Comment;
//import com.atlassian.jira.is
import com.atlassian.jira.rest.client.domain.EntityHelper;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.IssueFieldId;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.input.AttachmentInput;
import com.atlassian.jira.rest.client.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.domain.input.FieldInput;
import com.atlassian.jira.rest.client.domain.input.IssueInput;
import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.domain.input.LinkIssuesInput;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.java.objects.ResultDetails;



public class JIRABugLog 
{
	private Logger log;
	static JerseyJiraRestClientFactory factory;
	static URI jiraServerUri;
	static JiraRestClient restClient;

	public static String jiraURL;
	public static String userName;
	public static String password;
	public static String displayName;
	public static String assigneeURL;

	public static Hashtable<String, Object> bugdtls = new Hashtable<String, Object>();
	static //ystem.out.println(restClient.);
	NullProgressMonitor pm;

	public JIRABugLog( Properties miscProps, Logger log) {
		this.log=log;
		log.info(" logging bug in JIIRA  ");
		System.out.println("logging bug in JIRA ");


		jiraURL = miscProps.getProperty("bt_url");
		log.info(" URL: "+jiraURL);

		userName = miscProps.getProperty("bt_UserName");
		log.info(" Usename:"+userName);
		password = miscProps.getProperty("bt_Password");
		log.info(" Password:"+password);
		displayName = miscProps.getProperty("bt_DisplayName");
		log.info(" DisplayName:"+displayName);
		assigneeURL = miscProps.getProperty("bt_AssigneeURL");
		log.info(" bt_AssigneeURL:"+assigneeURL);
		bugdtls.put("key", miscProps.getProperty("productName"));
		bugdtls.put("component",miscProps.getProperty("componentName"));
		bugdtls.put("op_sys", miscProps.getProperty("op_sys"));
		bugdtls.put("platform", miscProps.getProperty("platform"));
		bugdtls.put("version",miscProps.getProperty("version"));
		bugdtls.put("priority",miscProps.getProperty("priority"));
		//bugdtls.put("issueType",miscProps.getProperty("issueType"));
		bugdtls.put("parentName",miscProps.getProperty("bt_ParentName"));
	}
	/*   public static void loadProperties(Properties miscProps) {

    	jiraURL = miscProps.getProperty("bt_url");

		userName = miscProps.getProperty("bt_UserName");
		password = miscProps.getProperty("bt_Password");
		displayName = miscProps.getProperty("bt_DisplayName");
		assigneeURL = miscProps.getProperty("bt_AssigneeURL");

		bugdtls.put("key", miscProps.getProperty("productName"));
		bugdtls.put("component",miscProps.getProperty("componentName"));
		bugdtls.put("op_sys", miscProps.getProperty("op_sys"));
		bugdtls.put("platform", miscProps.getProperty("platform"));
		bugdtls.put("version",miscProps.getProperty("version"));
		//bugdtls.put("priority",miscProps.getProperty("priority"));

	}*/
	public  void logBug(List<String> bugSummary) throws Exception
	{

		try {

			//loadProperties(miscProps);
			factory= new JerseyJiraRestClientFactory();
			jiraServerUri = new URI(jiraURL);

			restClient = factory.createWithBasicHttpAuthentication(jiraServerUri,userName,password);
			//final Project project = restClient.getProjectClient().getProject("http://localhost:8070", pm);

			pm = new NullProgressMonitor();

			System.out.println(bugdtls.get("key").toString());
			final IssueRestClient issueClient =restClient.getIssueClient();
			final Iterable<CimProject> metadataProjects = issueClient.getCreateIssueMetadata(
					new GetCreateIssueMetadataOptionsBuilder().withProjectKeys(bugdtls.get("key").toString()).withExpandedIssueTypesFields().build(), pm);

			// select project and issue
			System.out.println(Iterables.size(metadataProjects));
			final CimProject project = metadataProjects.iterator().next();
			final CimIssueType issueType = EntityHelper.findEntityByName(project.getIssueTypes(),"Bug");

			String summary = "#"+  bugSummary.get(0)+ " : "+ bugSummary.get(2);
			bugdtls.put("summary", summary);
			String comment = bugSummary.get(4);

			// grab the first component
			/* final Iterable<Object> allowedValuesForComponents = issueType.getField(IssueFieldId.COMPONENTS_FIELD).getAllowedValues();
		        System.out.println(allowedValuesForComponents);
		        System.out.println(allowedValuesForComponents.iterator().hasNext());

		        final BasicComponent component = (BasicComponent) allowedValuesForComponents.iterator().next();*/

			// grab the first priority
			final Iterable<Object> allowedValuesForPriority = issueType.getField(IssueFieldId.PRIORITY_FIELD).getAllowedValues();
			System.out.println(allowedValuesForPriority);
			log.info(" "+allowedValuesForPriority);
			System.out.println(allowedValuesForPriority.iterator().hasNext());
			log.info(" "+allowedValuesForPriority.iterator().hasNext());

			final BasicPriority priority = (BasicPriority) allowedValuesForPriority.iterator().next();

			// build issue input
			//final String summary = "My new issue!";
			final String description = bugSummary.get(4);
			//final BasicUser assignee = IntegrationTestUtil.USER1;
			final BasicUser assignee = new BasicUser(new URI(assigneeURL),userName,displayName);
			final List<String> affectedVersionsNames = Collections.emptyList();
			final DateTime dueDate = new DateTime(new Date().getTime());
			final ArrayList<String> fixVersionsNames = Lists.newArrayList(bugdtls.get("version").toString());

			// prepare IssueInput
			final IssueInputBuilder issueInputBuilder = new IssueInputBuilder(project, issueType, summary)
			.setDescription(description)
			.setAssignee(assignee)
			.setAffectedVersionsNames(affectedVersionsNames)
			.setDueDate(dueDate)
			.setPriority(priority);

			// create
			final BasicIssue basicCreatedIssue = issueClient.createIssue(issueInputBuilder.build(), pm);
			final Issue newIssue = issueClient.getIssue(basicCreatedIssue.getKey(), pm);
			String screenshotPath = comment.split("Screen Shot : ")[1];
			String fileName=screenshotPath.substring(screenshotPath.lastIndexOf("\\")+1,screenshotPath.length());
			FileInputStream in = new FileInputStream(screenshotPath);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte buf[] = new byte[1024];

			for (int readNum; (readNum = in.read(buf)) != -1;)
				bos.write(buf, 0, readNum);

			issueClient.addAttachments(pm,newIssue.getAttachmentsUri(),new AttachmentInput(fileName, new ByteArrayInputStream(bos.toByteArray())));
			System.out.println(basicCreatedIssue.getKey());
			log.info(basicCreatedIssue.getKey());
			log.info(" Logged bug in JIRA sucessfully");

		} catch (Throwable e) {
			log.error(" Unable to log defect in JIRA");
			log.error(" "+e.getCause());
			System.out.println("Unable to log defect in JIRA");
			System.out.println(e.getCause());
			e.printStackTrace();
			return;
		}
	}

	public  void logTestBugIfNotExists(List<String> bugSummary) throws Exception
	{

		try {

			//loadProperties(miscProps);
			factory= new JerseyJiraRestClientFactory();
			jiraServerUri = new URI(jiraURL);

			restClient = factory.createWithBasicHttpAuthentication(jiraServerUri,userName,password);
			//final Project project = restClient.getProjectClient().getProject("http://localhost:8070", pm);

			pm = new NullProgressMonitor();

			System.out.println(bugdtls.get("key").toString());
			final IssueRestClient issueClient =restClient.getIssueClient();

			final Iterable<CimProject> metadataProjects = issueClient.getCreateIssueMetadata(
					new GetCreateIssueMetadataOptionsBuilder().withProjectKeys(bugdtls.get("key").toString()).withExpandedIssueTypesFields().build(), pm);



			// select project and issue
			System.out.println(Iterables.size(metadataProjects));
			final CimProject project = metadataProjects.iterator().next();
			final CimIssueType issueType = EntityHelper.findEntityByName(project.getIssueTypes(),"Bug");

			String summary = "#"+  bugSummary.get(0)+ " : "+ bugSummary.get(2);
			bugdtls.put("summary", summary);
			String comment = bugSummary.get(4);



			//Get All issues and validate if the bug already exists
			final SearchRestClient searchClient = restClient.getSearchClient();
			final String jql = "project = \"" + project.getKey() + "\"";
			final SearchResult results = searchClient.searchJql(jql, null);
			System.out.println(results.getTotal());
			//assertEquals(1, results.getTotal());

			boolean flag=true;
			long bugID=0;
			String projectKey="";
			String bugKey="";
			// retrieve results. We know there's only one.
			for (final BasicIssue result : results.getIssues())
			{
				final Issue actual = issueClient.getIssue(result.getKey(), null);


				/* System.out.println(actual.getProject().getKey());
	                System.out.println(actual.getIssueType().getId().longValue());*/
				// System.out.println(actual.getSummary());

				if(actual.getSummary().equalsIgnoreCase(summary))
				{
					projectKey=actual.getProject().getKey();
					bugKey=actual.getKey();

					//issueClient.linkIssue(new LinkIssuesInput(actual.getProject().getKey(), toIssueKey, linkType), arg1)
					//IssueRestClient irc = restClient.getIssueClient();

					//issueClient.update(issue, ImmutableList.of(new FieldInput(fieldId, ComplexIssueInputFieldValue.with("value", "22"))), pm);

					flag=false;
					break;
				}
			}

			if(!flag)
			{
				System.out.println("Already Bug exists in JIRA With Bug  : " +bugKey + " In a project :"+projectKey  );
				System.out.println("Updating the comments of the Bug   : " +bugKey + " with the current issue reason" +bugSummary.get(4));
				final Issue updateIssue = issueClient.getIssue(bugKey, pm);
				final String contents1 = bugSummary.get(4);

				String screenshotPath = comment.split("Screen Shot : ")[1];
				String fileName=screenshotPath.substring(screenshotPath.lastIndexOf("\\")+1,screenshotPath.length());

				System.out.println("The comments URI is---" +updateIssue.getCommentsUri());
				issueClient.addComment(pm, updateIssue.getCommentsUri(), Comment.valueOf(contents1));

				FileInputStream in = new FileInputStream(screenshotPath);

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte buf[] = new byte[1024];

				for (int readNum; (readNum = in.read(buf)) != -1;)
					bos.write(buf, 0, readNum);

				issueClient.addAttachments(pm,updateIssue.getAttachmentsUri(),new AttachmentInput(fileName, new ByteArrayInputStream(bos.toByteArray())));



			}
			else
			{
				System.out.println("As there is no duplicate Logging a new bug In a project :"+projectKey  );	        
				// grab the first component
				/* final Iterable<Object> allowedValuesForComponents = issueType.getField(IssueFieldId.COMPONENTS_FIELD).getAllowedValues();
		        System.out.println(allowedValuesForComponents);
		        System.out.println(allowedValuesForComponents.iterator().hasNext());

		        final BasicComponent component = (BasicComponent) allowedValuesForComponents.iterator().next();*/

				// grab the first priority
				final Iterable<Object> allowedValuesForPriority = issueType.getField(IssueFieldId.PRIORITY_FIELD).getAllowedValues();
				System.out.println(allowedValuesForPriority);
				log.info(" "+allowedValuesForPriority);
				System.out.println(allowedValuesForPriority.iterator().hasNext());
				log.info(" "+allowedValuesForPriority.iterator().hasNext());

				final BasicPriority priority = (BasicPriority) allowedValuesForPriority.iterator().next();

				// build issue input
				//final String summary = "My new issue!";
				final String description = bugSummary.get(4);
				//final BasicUser assignee = IntegrationTestUtil.USER1;
				final BasicUser assignee = new BasicUser(new URI(assigneeURL),userName,displayName);
				final List<String> affectedVersionsNames = Collections.emptyList();
				final DateTime dueDate = new DateTime(new Date().getTime());
				final ArrayList<String> fixVersionsNames = Lists.newArrayList(bugdtls.get("version").toString());

				// prepare IssueInput
				final IssueInputBuilder issueInputBuilder = new IssueInputBuilder(project, issueType, summary)
				.setDescription(description)
				.setAssignee(assignee)
				.setAffectedVersionsNames(affectedVersionsNames)
				.setDueDate(dueDate)
				.setPriority(priority);

				// create
				final BasicIssue basicCreatedIssue = issueClient.createIssue(issueInputBuilder.build(), pm);
				final Issue newIssue = issueClient.getIssue(basicCreatedIssue.getKey(), pm);
				String screenshotPath = comment.split("Screen Shot : ")[1];
				String fileName=screenshotPath.substring(screenshotPath.lastIndexOf("\\")+1,screenshotPath.length());
				FileInputStream in = new FileInputStream(screenshotPath);

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte buf[] = new byte[1024];

				for (int readNum; (readNum = in.read(buf)) != -1;)
					bos.write(buf, 0, readNum);

				issueClient.addAttachments(pm,newIssue.getAttachmentsUri(),new AttachmentInput(fileName, new ByteArrayInputStream(bos.toByteArray())));
				System.out.println("Created New Bug : "+basicCreatedIssue.getKey());


				log.info(basicCreatedIssue.getKey());
				log.info(" Logged bug in JIRA sucessfully");
			}

		} catch (Throwable e) {
			log.error(" Unable to log defect in JIRA");
			log.error(" "+e.getCause());
			System.out.println("Unable to log defect in JIRA");
			System.out.println(e.getCause());
			e.printStackTrace();
			return;
		}
	}


	/*public  void logBugIfNotExists(List<String> bugSummary) throws Exception
	{

		try {

			   //loadProperties(miscProps);
			   factory= new JerseyJiraRestClientFactory();
	    	   jiraServerUri = new URI(jiraURL);

	    	   restClient = factory.createWithBasicHttpAuthentication(jiraServerUri,userName,password);
	    	  //final Project project = restClient.getProjectClient().getProject("http://localhost:8070", pm);

	    	   pm = new NullProgressMonitor();

			    System.out.println(bugdtls.get("key").toString());
			    final IssueRestClient issueClient =restClient.getIssueClient();
		        final Iterable<CimProject> metadataProjects = issueClient.getCreateIssueMetadata(
		                        new GetCreateIssueMetadataOptionsBuilder().withProjectKeys(bugdtls.get("key").toString()).withExpandedIssueTypesFields().build(), pm);



		        // select project and issue
		        System.out.println(Iterables.size(metadataProjects));
		        final CimProject project = metadataProjects.iterator().next();
		        final CimIssueType issueType = EntityHelper.findEntityByName(project.getIssueTypes(),"Bug");

		        String summary = "#"+  bugSummary.get(0)+ " : "+ bugSummary.get(2);
				bugdtls.put("summary", summary);
				String comment = bugSummary.get(4);



				 //Get All issues and validate if the bug already exists
		        final SearchRestClient searchClient = restClient.getSearchClient();
	            final String jql = "project = \"" + project.getKey() + "\"";
	            final SearchResult results = searchClient.searchJql(jql, null);
	            System.out.println(results.getTotal());
	            //assertEquals(1, results.getTotal());

	            boolean flag=true;
	            long bugID=0;
	            String projectKey="";
	            // retrieve results. We know there's only one.
	            for (final BasicIssue result : results.getIssues())
	            {
	                final Issue actual = issueClient.getIssue(result.getKey(), null);

	                System.out.println(actual.getProject().getKey());
	                System.out.println(actual.getIssueType().getId().longValue());
	               // System.out.println(actual.getSummary());

	                if(actual.getSummary().equalsIgnoreCase(summary))
	                {
	                 projectKey=actual.getProject().getKey();
	                 bugID=actual.getIssueType().getId().longValue();
	                 flag=false;
	                 break;
	                }
	            }

	            if(!flag)
	            {
	            	System.out.println("Already Bug exists in JIRA With Bug ID : " +bugID + " In a project :"+projectKey  );
	            }
	            else
	            {
	            	System.out.println("As there is no duplicate Logging a new bug In a project :"+projectKey  );	        
		        // grab the first component
		        final Iterable<Object> allowedValuesForComponents = issueType.getField(IssueFieldId.COMPONENTS_FIELD).getAllowedValues();
		        System.out.println(allowedValuesForComponents);
		        System.out.println(allowedValuesForComponents.iterator().hasNext());

		        final BasicComponent component = (BasicComponent) allowedValuesForComponents.iterator().next();

		        // grab the first priority
		        final Iterable<Object> allowedValuesForPriority = issueType.getField(IssueFieldId.PRIORITY_FIELD).getAllowedValues();
		        System.out.println(allowedValuesForPriority);
		        log.info(" "+allowedValuesForPriority);
		        System.out.println(allowedValuesForPriority.iterator().hasNext());
		        log.info(" "+allowedValuesForPriority.iterator().hasNext());

		        final BasicPriority priority = (BasicPriority) allowedValuesForPriority.iterator().next();

		        // build issue input
		        //final String summary = "My new issue!";
		        final String description = bugSummary.get(4);
		        //final BasicUser assignee = IntegrationTestUtil.USER1;
		        final BasicUser assignee = new BasicUser(new URI(assigneeURL),userName,displayName);
		        final List<String> affectedVersionsNames = Collections.emptyList();
		        final DateTime dueDate = new DateTime(new Date().getTime());
		        final ArrayList<String> fixVersionsNames = Lists.newArrayList(bugdtls.get("version").toString());

		        // prepare IssueInput
		        final IssueInputBuilder issueInputBuilder = new IssueInputBuilder(project, issueType, summary)
		                        .setDescription(description)
		                        .setAssignee(assignee)
		                        .setAffectedVersionsNames(affectedVersionsNames)
		                        .setDueDate(dueDate)
		                        .setPriority(priority);

		        // create
		        final BasicIssue basicCreatedIssue = issueClient.createIssue(issueInputBuilder.build(), pm);
		        System.out.println(basicCreatedIssue.getKey());
		        log.info(basicCreatedIssue.getKey());
		        log.info(" Logged bug in JIRA sucessfully");
	         }

		} catch (Throwable e) {
			log.error(" Unable to log defect in JIRA");
			log.error(" "+e.getCause());
			System.out.println("Unable to log defect in JIRA");
			System.out.println(e.getCause());
			e.printStackTrace();
			return;
		}
	}*/

	/*	public static void main(String[] args) throws IOException, JSONException, URISyntaxException
	{
		//new JIRABugLog(new Properties(),Logger.getLogger("")).searchAndCreateSubTask();
	}*/

	/*public void searchAllIssues() throws IOException, URISyntaxException {
		factory= new JerseyJiraRestClientFactory();
	   	   jiraServerUri = new URI("http://localhost:8070/");
	   	  String project="DEMO";
	   	   restClient = factory.createWithBasicHttpAuthentication(jiraServerUri,"vnimmala","Test*123");
	   	  //final Project project = restClient.getProjectClient().getProject("http://localhost:8070", pm);

	   	   pm = new NullProgressMonitor();
        try {
            // create issue

            final IssueRestClient issueClient = restClient.getIssueClient();
            final SearchRestClient searchClient = restClient.getSearchClient();
            final String jql = "project = \"" + project + "\"";
            final SearchResult results = searchClient.searchJql(jql, null);
            System.out.println(results.getTotal());
            //assertEquals(1, results.getTotal());

            // retrieve results. We know there's only one.
            for (final BasicIssue result : results.getIssues()) {
                final Issue actual = issueClient.getIssue(result.getKey(), null);

                System.out.println(actual.getProject().getKey());
                System.out.println(actual.getIssueType().getId().longValue());
                System.out.println(actual.getSummary());
            }

            // post 2.0.0-m25 we can delete issue to reduce clutter
           // issueClient.deleteIssue(issue.getKey(), false).claim();
        } 
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        finally {
            if (restClient != null) {
                restClient.close();
            }

    }*/

	/*public void searchAndCreateSubTask(List<String> bugSummary) throws IOException, JSONException, URISyntaxException 
	{

		 factory= new JerseyJiraRestClientFactory();
  	   jiraServerUri = new URI(jiraURL);

 	   restClient = factory.createWithBasicHttpAuthentication(jiraServerUri,userName,password);
 	  //final Project project = restClient.getProjectClient().getProject("http://localhost:8070", pm);
 	   final IssueRestClient issueClient =restClient.getIssueClient();
 	   pm = new NullProgressMonitor();

       final long TASK_TYPE_ID = 3L; // JIRA magic value
       final long SUBTASK_TYPE_ID = 5L; // JIRA magic value
       final String SUMMARY = "summary";

       final Iterable<CimProject> metadataProjects = issueClient.getCreateIssueMetadata(
               new GetCreateIssueMetadataOptionsBuilder().withProjectKeys(bugdtls.get("key").toString()).withExpandedIssueTypesFields().build(), pm);



       // select project and issue
       System.out.println(Iterables.size(metadataProjects));
       final CimProject project = metadataProjects.iterator().next();

       String summary =  bugSummary.get(2);
		bugdtls.put("summary", summary);
		String comment = bugSummary.get(4);



		 //Get All issues and validate if the bug already exists
       final SearchRestClient searchClient = restClient.getSearchClient();
       final String jql = "project = \"" + project.getKey() + "\""+" AND parent = \"" + bugdtls.get("parentName") + "\""+" AND status != \"" + "CLOSED" + "\"";
       final SearchResult results = searchClient.searchJql(jql, null);
       System.out.println(results.getTotal());
       //assertEquals(1, results.getTotal());


       //assertEquals(1, results.getTotal());

       boolean flag=true;
       long bugID=0;
       String projectKey="";
       String bugKey="";
       final IssueRestClient client = restClient.getIssueClient();

       // retrieve results. We know there's only one.
       for (final BasicIssue result : results.getIssues())
       {
           final Issue actual = client.getIssue(result.getKey(), null);

          System.out.println(actual.getProject().getKey());
           System.out.println(actual.getIssueType().getId().longValue());
           System.out.println(actual.getSummary());

           if(actual.getSummary().equalsIgnoreCase(summary))
           {
            projectKey=actual.getProject().getKey();
            bugKey=actual.getKey();
            flag=false;
            break;
           }
       }

       if(!flag)
       {
       	System.out.println("Already Bug exists in JIRA With Bug ID : " +bugKey + " In a project :"+projectKey  );
        System.out.println("Updating the comments of the Bug   : " +bugKey + " with the current issue reason :" +bugSummary.get(4));
   	    final Issue updateIssue = issueClient.getIssue(bugKey, pm);
   	    final String contents1 = bugSummary.get(4);
   	    issueClient.addComment(pm, updateIssue.getCommentsUri(), Comment.valueOf(contents1));
       }
       else
       {


        try {

        	System.out.println("As there is no duplicate Logging a new bug In a project :"+project.getKey()  );	        

	        // grab the first priority
	        final Iterable<Object> allowedValuesForPriority = issueType.getField(IssueFieldId.PRIORITY_FIELD).getAllowedValues();
	        System.out.println(allowedValuesForPriority);
	        log.info(" "+allowedValuesForPriority);
	        System.out.println(allowedValuesForPriority.iterator().hasNext());
	        log.info(" "+allowedValuesForPriority.iterator().hasNext());

	        final BasicPriority priority = (BasicPriority) allowedValuesForPriority.iterator().next();

	        // build issue input
	        //final String summary = "My new issue!";
	        final String description = bugSummary.get(4);
	        //final BasicUser assignee = IntegrationTestUtil.USER1;
	        final BasicUser assignee = new BasicUser(new URI(assigneeURL),userName,displayName);
	        final List<String> affectedVersionsNames = Collections.emptyList();
	        final DateTime dueDate = new DateTime(new Date().getTime());
	        final ArrayList<String> fixVersionsNames = Lists.newArrayList(bugdtls.get("version").toString());

	        // prepare IssueInput
	        final IssueInputBuilder issueInputBuilder = new IssueInputBuilder(project.getKey(),SUBTASK_TYPE_ID,"child")
	                        .setDescription(description)
	                        .setAssignee(assignee)
	                        .setAffectedVersionsNames(affectedVersionsNames)
	                        .setDueDate(dueDate).setSummary(summary);


	        // create
	        issueInputBuilder.setFieldValue("parent", ComplexIssueInputFieldValue.with("key",bugdtls.get("parentName")));
            final BasicIssue child = client.createIssue(issueInputBuilder.build(), null);

	       // final BasicIssue basicCreatedIssue = issueClient.createIssue(issueInputBuilder.build(), pm);


	        System.out.println("Created New Sub task : "+child.getKey());
	       // System.out.println(child.());
	        log.info(child.getKey());
	        log.info(" Logged bug in JIRA sucessfully");

            // create subtask
        	   final IssueInputBuilder childBuilder = new IssueInputBuilder("DEMO", SUBTASK_TYPE_ID, "child");
        	   childBuilder.setFieldValue("parent", ComplexIssueInputFieldValue.with("key","DEMO-90"));
               final BasicIssue child = client.createIssue(childBuilder.build(), null);

            System.out.println("Child : " + child);

        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
       }
    }
	 */


	/*public void createTaskAndSubTask() throws IOException, JSONException, URISyntaxException 
	{

		factory= new JerseyJiraRestClientFactory();
   	   jiraServerUri = new URI("http://localhost:8070/");

   	   restClient = factory.createWithBasicHttpAuthentication(jiraServerUri,"vnimmala","Test*123");
   	  //final Project project = restClient.getProjectClient().getProject("http://localhost:8070", pm);

   	   pm = new NullProgressMonitor();

       final long TASK_TYPE_ID = 3L; // JIRA magic value
       final long SUBTASK_TYPE_ID = 5L; // JIRA magic value
       final String SUMMARY = "summary";

        final IssueInputBuilder parentBuilder = new IssueInputBuilder("DEMO",TASK_TYPE_ID,"parent");
        final IssueInput parentInput = parentBuilder.build();

        try {
            // create task
            final IssueRestClient client = restClient.getIssueClient();
            final BasicIssue parent =  client.createIssue(parentInput, null);

            //assertNotNull(parent.getKey());
            System.out.println("parent: " + parent);

            // create subtask
            final IssueInputBuilder childBuilder = new IssueInputBuilder("DEMO", SUBTASK_TYPE_ID, "child");
            childBuilder.setFieldValue("parent", ComplexIssueInputFieldValue.with("key", parent.getKey()));
            final BasicIssue child = client.createIssue(childBuilder.build(), null);

            System.out.println("Child : " + child);
            //assertNotNull(child.getKey());

            // retrieve parent
            final Issue actual = client.getIssue(parent.getKey()).claim();

            assertEquals(PROJECT_KEY, actual.getProject().getKey());
            assertEquals("parent", actual.getSummary());

            // retrieve children, verify 'parent' points back to parent.
            for (final Subtask subtask : actual.getSubtasks()) {
                final Issue actualChild = client.getIssue(subtask.getIssueKey()).claim();
                final JSONObject json = (JSONObject) actualChild.getField("parent").getValue();
                assertEquals(parent.getKey(), json.getString("key"));
            }

            // post 2.0.0-m25 we can delete issue to reduce clutter
            client.deleteIssue(parent.getKey(), true).claim();
         finally {
            if (restClient != null) {
                restClient.close();
            }
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    }


	public void createMinimalIssueAndSubtask() throws IOException, JSONException, URISyntaxException 
	{

		factory= new JerseyJiraRestClientFactory();
   	   jiraServerUri = new URI("http://localhost:8070/");

   	   restClient = factory.createWithBasicHttpAuthentication(jiraServerUri,"vnimmala","Test*123");
   	  //final Project project = restClient.getProjectClient().getProject("http://localhost:8070", pm);

   	   pm = new NullProgressMonitor();

       final long TASK_TYPE_ID = 3L; // JIRA magic value
       final long SUBTASK_TYPE_ID = 5L; // JIRA magic value
       final String SUMMARY = "summary";

        final IssueInputBuilder parentBuilder = new IssueInputBuilder("DEMO",TASK_TYPE_ID,"parent");
        final IssueInput parentInput = parentBuilder.build();

        try {
            // create task
            final IssueRestClient client = restClient.getIssueClient();
            final BasicIssue parent =  client.createIssue(parentInput, null);

            //assertNotNull(parent.getKey());
            System.out.println("parent: " + parent);

            // create subtask
            final IssueInputBuilder childBuilder = new IssueInputBuilder("DEMO", SUBTASK_TYPE_ID, "child");
            childBuilder.setFieldValue("parent", ComplexIssueInputFieldValue.with("key", parent.getKey()));
            final BasicIssue child = client.createIssue(childBuilder.build(), null);

            System.out.println("Child : " + child);
            //assertNotNull(child.getKey());

            // retrieve parent
            final Issue actual = client.getIssue(parent.getKey()).claim();

            assertEquals(PROJECT_KEY, actual.getProject().getKey());
            assertEquals("parent", actual.getSummary());

            // retrieve children, verify 'parent' points back to parent.
            for (final Subtask subtask : actual.getSubtasks()) {
                final Issue actualChild = client.getIssue(subtask.getIssueKey()).claim();
                final JSONObject json = (JSONObject) actualChild.getField("parent").getValue();
                assertEquals(parent.getKey(), json.getString("key"));
            }

            // post 2.0.0-m25 we can delete issue to reduce clutter
            client.deleteIssue(parent.getKey(), true).claim();
         finally {
            if (restClient != null) {
                restClient.close();
            }
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    }*/

}
