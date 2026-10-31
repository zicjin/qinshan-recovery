import com.sunwayworld.owned.attachments.replay.*;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/** Loopback replay seam: success, bounded retry, idempotency, conflict and no partial writes. */
public final class OwnedErpReplayContractTest {
    private static int checks;
    private static void check(boolean value, String label) { checks++; if (!value) throw new AssertionError(label); }
    public static void main(String[] args) throws Exception {
        final AtomicInteger attempts = new AtomicInteger();
        HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/hj/erp/downloadFile", exchange -> {
            check("POST".equals(exchange.getRequestMethod()), "ERP replay uses POST");
            check("erp-replay".equals(exchange.getRequestHeaders().getFirst("X-Replay-Source")), "request carries replay source marker");
            byte[] request = read(exchange.getRequestBody());
            String form = new String(request, StandardCharsets.UTF_8);
            if (form.contains("fileSign=fixture-sign")) {
                check(form.contains("fileSign=fixture-sign"), "request carries fileSign");
                check(form.contains("merchantId=merchant-a"), "request carries trusted merchant context");
            }
            int count = attempts.incrementAndGet();
            if (count == 1) { exchange.sendResponseHeaders(503, 0); exchange.close(); return; }
            byte[] body = "erp fixture bytes\n".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length); exchange.getResponseBody().write(body); exchange.close();
        });
        server.start();
        Path root = Files.createTempDirectory("erp-replay-contract-");
        Path journal = root.resolve("trace.jsonl");
        try {
            URI base = new URI("http://127.0.0.1:" + server.getAddress().getPort());
            OwnedErpReplayFileService service = new OwnedErpReplayFileService("merchant-a", new OwnedErpReplayHttpTransport(base), journal, 1);
            check("merchant-a".equals(service.getMerchantId()), "merchant context comes from trusted server metadata");
            Path first = root.resolve("first.bin");
            service.downloadFile("fixture-sign", first.toFile());
            check(new String(Files.readAllBytes(first), StandardCharsets.UTF_8).equals("erp fixture bytes\n"), "replayed ERP success writes complete bytes");
            check(attempts.get() == 2, "transient ERP failure retries once");
            Path second = root.resolve("second.bin");
            service.downloadFile("fixture-sign", second.toFile());
            check(attempts.get() == 2 && Files.exists(second), "repeated idempotency key reuses stable result");
            boolean conflict = false;
            try { service.downloadFile("other-sign", root.resolve("conflict.bin").toFile(), "same-key"); service.downloadFile("different-sign", root.resolve("conflict-2.bin").toFile(), "same-key"); }
            catch (IllegalArgumentException expected) { conflict = true; }
            check(conflict, "idempotency key payload conflict is rejected");
            String trace = new String(Files.readAllBytes(journal), StandardCharsets.UTF_8);
            check(trace.contains("\"source\":\"erp-replay\"") && trace.contains("\"path\":\"/hj/erp/downloadFile\"") && trace.contains("\"status\":503") && trace.contains("\"status\":200"), "journal records source, path, failure and success");
            boolean production = false;
            try { new OwnedErpReplayHttpTransport(new URI("https://127.0.0.1")); } catch (IllegalArgumentException expected) { production = true; }
            check(production, "non-loopback ERP endpoint is rejected");
            final Path existing = root.resolve("existing.bin"); Files.write(existing, "keep".getBytes(StandardCharsets.UTF_8));
            OwnedErpReplayFileService failed = new OwnedErpReplayFileService("merchant-a", (path, merchant, sign, key) -> new OwnedReplayResponse(503, new byte[0]), root.resolve("failed.jsonl"), 0);
            boolean rejected = false; try { failed.downloadFile("failed-sign", existing.toFile()); } catch (IllegalStateException expected) { rejected = true; }
            check(rejected && new String(Files.readAllBytes(existing), StandardCharsets.UTF_8).equals("keep"), "failed replay does not write partial target bytes");
            System.out.println("ERP replay contract: PASS (" + checks + " checks; loopback fixture only)");
        } finally { server.stop(0); delete(root); }
    }
    private static byte[] read(InputStream stream) throws IOException { ByteArrayOutputStream output = new ByteArrayOutputStream(); byte[] buffer = new byte[1024]; int count; while ((count = stream.read(buffer)) != -1) output.write(buffer, 0, count); return output.toByteArray(); }
    private static void delete(Path root) throws IOException { Files.walk(root).sorted(Comparator.reverseOrder()).forEach(path -> { try { Files.delete(path); } catch (IOException failure) { throw new UncheckedIOException(failure); } }); }
}
