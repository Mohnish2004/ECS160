package com.ecs160.hw;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.ecs160.hw.model.Commit;
import com.ecs160.hw.model.Owner;
import com.ecs160.hw.model.Repo;
import com.ecs160.hw.util.MetricsHandler;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest 
{   
    private List<Repo> testRepos;
    private Repo testRepoWithCommits;

    @Before
    public void setUp() {
        testRepos = new ArrayList<>();
        testRepos.add(new Repo("repo1", "owner1", "http://github.com/owner1/repo1", "http://github.com/owner1/repo1.git", 100, 500, "Java", 20, "2024-01-01T00:00:00Z"));
        testRepos.add(new Repo("repo2", "owner2", "http://github.com/owner2/repo2", "http://github.com/owner2/repo2.git", 200, 300, "Java", 15, "2024-01-02T00:00:00Z"));
        testRepos.add(new Repo("repo3", "owner3", "http://github.com/owner3/repo3", "http://github.com/owner3/repo3.git", 50, 800, "Java", 10, "2024-01-03T00:00:00Z"));
        
        testRepoWithCommits = new Repo("test-repo", "test-owner", "http://github.com/test-owner/test-repo", "http://github.com/test-owner/test-repo.git", 50, 100, "Java", 5, "2024-01-01T00:00:00Z");

        Owner author = new Owner("testuser", 123L, "http://github.com/testuser", false);

        Commit commit1 = new Commit("sha1", "First commit", ZonedDateTime.now(), author);
        commit1.setModifiedFiles(Arrays.asList("App.java", "Main.java", "Utils.java"));
        testRepoWithCommits.addCommit(commit1);
        
        Commit commit2 = new Commit("sha2", "Second commit", ZonedDateTime.now(), author);
        commit2.setModifiedFiles(Arrays.asList("App.java", "Main.java"));
        testRepoWithCommits.addCommit(commit2);
        
        Commit commit3 = new Commit("sha3", "Third commit", ZonedDateTime.now(), author);
        commit3.setModifiedFiles(Arrays.asList("App.java", "Config.java"));
        testRepoWithCommits.addCommit(commit3);
        
        Commit commit4 = new Commit("sha4", "Fourth commit", ZonedDateTime.now(), author);
        commit4.setModifiedFiles(Arrays.asList("Config.java", "Utils.java"));
        testRepoWithCommits.addCommit(commit4);
    }

    @Test
    public void testGetTotalStars()
    {
        int totalStars = MetricsHandler.getTotalStars(testRepos);
        assertEquals(1600, totalStars);
    }

    @Test
    public void testGetTotalForks() {
        int totalForks = MetricsHandler.getTotalForks(testRepos);
        assertEquals(350, totalForks);
    }

    @Test
    public void testGetTotalOpenIssues() {
        int totalIssues = MetricsHandler.getTotalOpenIssues(testRepos);
        assertEquals(45, totalIssues);
    }

    @Test
    public void testGetMostModifiedFiles() {
        List<String> topFiles = MetricsHandler.getMostModifiedFilesInRecentCommits(testRepoWithCommits, 3);
        
        assertEquals(3, topFiles.size());
        assertEquals("App.java", topFiles.get(0));
        assertTrue(topFiles.contains("Main.java"));
        assertTrue(topFiles.contains("Config.java"));
    }

    @Test
    public void testGetNewCommitCountFromForkedRepos() {
        List<Repo> forkedRepos = new ArrayList<>();
        
        Repo fork1 = new Repo("fork1", "owner1", "http://github.com/owner1/fork1", "http://github.com/owner1/fork1.git", 0, 0, "Java", 0, "2024-01-01T00:00:00Z");
        fork1.setCommitAfterForkCount(15);
        forkedRepos.add(fork1);
        
        Repo fork2 = new Repo("fork2", "owner2", "http://github.com/owner2/fork2", "http://github.com/owner2/fork2.git", 0, 0, "Java", 0, "2024-01-02T00:00:00Z");
        fork2.setCommitAfterForkCount(25);
        forkedRepos.add(fork2);
        
        Repo fork3 = new Repo("fork3", "owner3", "http://github.com/owner3/fork3", "http://github.com/owner3/fork3.git", 0, 0, "Java", 0, "2024-01-03T00:00:00Z");
        fork3.setCommitAfterForkCount(10);
        forkedRepos.add(fork3);
        
        int totalCommits = MetricsHandler.getNewCommitCountFromForkedRepos(forkedRepos);
        assertEquals(50, totalCommits);
    }

    @Test
    public void testIsRealCodebaseWithSourceCode() {
        Repo repo = new Repo("codebase", "owner", "http://github.com/owner/codebase", "http://github.com/owner/codebase.git", 0, 0, "Java", 0, "2024-01-03T00:00:00Z");
        repo.addFile("App.java");
        repo.addFile("Main.java");
        repo.addFile("Utils.java");
        repo.addFile("Config.java");
        repo.addFile("Test.java");
        repo.addFile("README.md");
        repo.addFile("LICENSE");
        repo.addFile("pom.xml");
        
        assertTrue(MetricsHandler.isRealCodebase(repo));
    }

    @Test
    public void testIsRealCodebaseTutorialRepo() {
        Repo repo = new Repo("tutorial", "owner", "http://github.com/owner/tutorial", "http://github.com/owner/tutorial.git", 0, 0, "Java", 0, "2024-01-03T00:00:00Z");
        repo.addFile("README.md");
        repo.addFile("TUTORIAL.md");
        repo.addFile("LICENSE");
        repo.addFile("test.txt");
        repo.addFile("docs.txt");
        repo.addFile("App.java");
        repo.addFile("Main.java");
        
        assertTrue(!MetricsHandler.isRealCodebase(repo));
    }

    @Test
    public void testHasSourceCodeExtension() {
        assertTrue(MetricsHandler.hasSourceCodeExtension("App.java"));
        assertTrue(MetricsHandler.hasSourceCodeExtension("main.c"));
        assertTrue(MetricsHandler.hasSourceCodeExtension("server.rs"));
        assertTrue(!MetricsHandler.hasSourceCodeExtension("README.md"));
        assertTrue(!MetricsHandler.hasSourceCodeExtension("LICENSE"));
    }
}
