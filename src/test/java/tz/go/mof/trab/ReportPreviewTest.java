package tz.go.mof.trab;

import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Standalone report preview — no database, no Spring context.
 * Run: mvn exec:java -Dexec.mainClass="tz.go.mof.trab.ReportPreviewTest" -Dexec.classpathScope="test"
 * Or just run as a Java main class from IDE.
 * Output: report-preview.pdf in project root
 */
public class ReportPreviewTest {

    public static void main(String[] args) throws Exception {
        // 1. Set up Thymeleaf standalone
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");

        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);

        // 2. Load coat of arms as base64 data URI
        String logoBase64 = loadLogoBase64();

        // 3. Build sample data for Revenue Summary Report
        Map<String, Object> variables = buildRevenueSummaryData(logoBase64);

        // 4. Render HTML
        Context context = new Context();
        context.setVariables(variables);
        String html = engine.process("reports/revenue-summary-report", context);

        // 5. Render to PDF
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();

        File outputFile = new File("report-preview.pdf");
        try (OutputStream os = new FileOutputStream(outputFile)) {
            renderer.createPDF(os);
        }

        System.out.println("===========================================");
        System.out.println("PDF generated: " + outputFile.getAbsolutePath());
        System.out.println("===========================================");
    }

    private static String loadLogoBase64() {
        try (InputStream is = ReportPreviewTest.class.getClassLoader()
                .getResourceAsStream("static/images/coat-of-arms.png")) {
            if (is == null) {
                System.out.println("WARNING: coat-of-arms.png not found, using placeholder");
                return "";
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int len;
            while ((len = is.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            System.out.println("WARNING: could not load logo: " + e.getMessage());
            return "";
        }
    }

    private static Map<String, Object> buildRevenueSummaryData(String logoBase64) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("logoBase64", logoBase64);
        vars.put("dateRange", "01/01/2024 - 31/12/2024");
        vars.put("generatedDate", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));

        // Sample revenue rows
        List<Map<String, Object>> revenues = new ArrayList<>();
        revenues.add(makeRevenue("Appeal Filing Fee", 245, 210, new BigDecimal("1225000000"), new BigDecimal("1050000000"), 85.7));
        revenues.add(makeRevenue("Application Fee", 180, 142, new BigDecimal("900000000"), new BigDecimal("710000000"), 78.9));
        revenues.add(makeRevenue("Certified Copy Fee", 95, 90, new BigDecimal("47500000"), new BigDecimal("45000000"), 94.7));
        revenues.add(makeRevenue("Hearing Fee", 320, 198, new BigDecimal("1600000000"), new BigDecimal("990000000"), 61.9));
        revenues.add(makeRevenue("Penalty Fee", 60, 22, new BigDecimal("300000000"), new BigDecimal("110000000"), 36.7));

        vars.put("revenues", revenues);
        vars.put("totalCategories", revenues.size());
        vars.put("grandTotalBills", 900);
        vars.put("grandTotalPaid", 662);
        vars.put("grandTotalBilled", new BigDecimal("4072500000"));
        vars.put("grandTotalCollected", new BigDecimal("2905000000"));
        vars.put("overallCollectionRate", 71.3);

        return vars;
    }

    private static Map<String, Object> makeRevenue(String appType, int billCount, int paidCount,
                                                     BigDecimal totalBilled, BigDecimal totalCollected,
                                                     double collectionRate) {
        Map<String, Object> row = new HashMap<>();
        row.put("appType", appType);
        row.put("billCount", billCount);
        row.put("paidCount", paidCount);
        row.put("totalBilled", totalBilled);
        row.put("totalCollected", totalCollected);
        row.put("collectionRate", collectionRate);
        return row;
    }
}
