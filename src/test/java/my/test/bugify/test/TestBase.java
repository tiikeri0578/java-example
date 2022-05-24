package my.test.bugify.test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import my.test.bugify.model.Issue;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.testng.SkipException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.google.gson.JsonParser.parseString;
import static org.apache.http.client.fluent.Request.Get;
import static org.apache.http.client.fluent.Request.Post;

public class TestBase {

    private Executor getExecutor() {
        return Executor.newInstance().auth("288f44776e7bec4bf44fdfeb1e646490", "");
    }

    public boolean isIssueOpen(int issueId) throws IOException {

        String json = getExecutor().execute(Request.Get("http://bugify.stqa.ru/api/issues/" + issueId + ".json")).returnContent().asString();
        JsonElement parsed = parseString(json);
        JsonElement issues = parsed.getAsJsonObject().get("issues");
        List<Issue> issueData = new Gson().fromJson(issues, new TypeToken<List<Issue>>() {
        }.getType());
        String issueStatus = issueData.get(0).getState_name();

        return (issueStatus.equals("resolved")) || (issueStatus.equals("closed")) || (issueStatus.equals("deleted"));
    }

    public void skipIfNotFixed(int issueId) throws IOException {
        if (isIssueOpen(issueId)) {
            throw new SkipException("Ignored because of issue " + issueId);
        }
    }

    Set<Issue> getIssues() throws IOException {
        String json = getExecutor().execute(Get("http://bugify.stqa.ru/api/issues.json?limit=1000")).returnContent().asString();
        JsonElement parsed = parseString(json);
        JsonElement issues = parsed.getAsJsonObject().get("issues");
        return new Gson().fromJson(issues, new TypeToken<Set<Issue>>() {
        }.getType());
    }

    int createIssue(Issue newIssue) throws IOException {
        String json = getExecutor().execute(Post("http://bugify.stqa.ru/api/issues.json")
                        .bodyForm(new BasicNameValuePair("subject", newIssue.getSubject()), new BasicNameValuePair("description", newIssue.getDescription())))
                .returnContent().asString();
        JsonElement parsed = parseString(json);
        return parsed.getAsJsonObject().get("issue_id").getAsInt();
    }
}
