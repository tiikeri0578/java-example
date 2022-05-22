package my.test.bugify.test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import my.test.bugify.model.Issue;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.testng.SkipException;

import java.io.IOException;
import java.util.List;

public class TestBase {

    private Executor getExecutor() {
        return Executor.newInstance().auth("288f44776e7bec4bf44fdfeb1e646490", "");
    }

    public boolean isIssueOpen(int issueId) throws IOException {

        String json = getExecutor().execute(Request.Get("http://bugify.stqa.ru/api/issues/" + issueId + ".json")).returnContent().asString();
        JsonElement parsed = new JsonParser().parse(json);
        JsonElement issues = parsed.getAsJsonObject().get("issues");
        List<Issue> issueData = new Gson().fromJson(issues, new TypeToken<List<Issue>>() {}.getType());
        String issueStatus = issueData.get(0).getState_name();

        if ((issueStatus.equals("resolved")) || (issueStatus.equals("closed")) || (issueStatus.equals("deleted"))) {
            return true;
        } else return false;
    }

    public void skipIfNotFixed(int issueId) throws IOException {
        if (isIssueOpen(issueId)) {
            throw new SkipException("Ignored because of issue " + issueId);
        }
    }
}
