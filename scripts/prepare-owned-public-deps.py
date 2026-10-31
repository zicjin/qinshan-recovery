#!/usr/bin/env python3
"""Extract only pinned public libraries for owned compatibility compilation/tests."""
import hashlib
from pathlib import Path
import sys
import zipfile

root = Path(__file__).resolve().parents[1]
output = Path(sys.argv[1])
artifact_root = Path(__import__("os").environ.get("LIMS_EXTERNAL_ARTIFACT_ROOT", root / ".external-materials"))
run_jar = Path(__import__("os").environ.get(
    "SUPPLIER_RUN_JAR", artifact_root / "gikam-third-czjky-14.1.5-run.jar"
))
dependencies = {
    "SparseBitSet-1.2.jar": "91e6b318c901a0f2dd1f6ce781d62474435ae627d22fbac9b21bbc39ffd804b6",
    "commons-math3-3.6.1.jar": "1e56d7b058d28b65abd256b8458e3885b674c1d588fa43cd7d1cbb9c7ef2b308",
    "poi-4.1.2.jar": "ab1612406541968434044b2defad58aa8b657cad073baa22a04faaf9d7fb9d1c",
    "spring-boot-2.3.4.RELEASE.jar": "6f31d29d22fc2003fc77b90db2c028a1e2e8d4e6b2a6ed61b07269aeb1c3d4a9",
    "spring-boot-autoconfigure-2.3.4.RELEASE.jar": "0c5645b638f3422d96411e4c47b6b5c217408d8e4314ff8fcb2aff29f7cba7a7",
    "spring-data-commons-2.3.4.RELEASE.jar": "9238de74decf24aa8e9ab2f0206f9e20f8a42096651f558160a7125014453f55",
    "spring-data-keyvalue-2.3.4.RELEASE.jar": "af8547a4f6fea717f76456af6150ba814b585dfe2b83c3fa5f572f97d5398914",
    "spring-data-redis-2.3.4.RELEASE.jar": "fcd476ac2032bac7d6835e789b25578aa58a1ff1c980c43f7887f74fb4628db2",
    "lettuce-core-5.3.4.RELEASE.jar": "775efcc0364cdde963bbfd3dd9fbaf16991bc8b020700ba4fb89903d23d066e9",
    "reactor-core-3.3.10.RELEASE.jar": "f0b92788cc6d3ca9e5de0fed317faae629443ee348865e620d9160c83b218f59",
    "reactive-streams-1.0.3.jar": "1dee0481072d19c929b623e155e14d2f6085dc011529a0a0dbefc84cf571d865",
    "netty-common-4.1.52.Final.jar": "0d1f101e4db7d530ed04d7cabd57d95c0750f059bb6b3fadfed0c80d4734c717",
    "netty-handler-4.1.52.Final.jar": "2cdc21fbfae04940c85929039779c93f51f618f5efb216079319dcfcf32a55c1",
    "netty-resolver-4.1.52.Final.jar": "6eeb72a233979cb45b0b4d6d46c5a41332c57c0cf8a803b1efc60dbbecba09a6",
    "netty-buffer-4.1.52.Final.jar": "41ca1035aee6e0f8177597d0a7610a5aa2c6e6fac744ac81fde4e5e58f639675",
    "netty-codec-4.1.52.Final.jar": "3c7e78dcce7e353d86ac7dba2c251ab5cddd4eb86eaa889cfe4229782b828e52",
    "netty-transport-4.1.52.Final.jar": "9a3e6f8c0e55de363eb1ea10fe781797eca394e62186df2ae0b4eb2bce0b4541",
    "netty-codec-http-4.1.52.Final.jar": "b5da28d0ea878b07d3111acf5e4faf41288ef2302b26414190fdd1a7d973c80c",
    "commons-pool2-2.8.1.jar": "16154621c8d292278d3849c7df6be8de8cdc9feb7479b2b2a0a917ee847bfce6",
    "spring-core-5.2.9.RELEASE.jar": "7cbae8ae5ccf5f238b101596c6a6d87e9451d98fbd9c4a6af1dac49cf1c0538a",
    "spring-context-5.2.9.RELEASE.jar": "cebfe9490d348d288fee54a526de69ea3378c92ae4b561511dd69692ac8201a9",
    "fastjson-1.2.83.jar": "641a4d65ab32fbfdccd9c718e3f83ebc4caabdb5e4fe5b3d51527c5fe692631d",
    "mybatis-3.5.6.jar": "f32720f23f85d073da04e8d0742f2bdbbd90ca90269057407be4283a673b8f35",
    "spring-web-5.2.9.RELEASE.jar": "76355c69937b0e8153169608532e8424cec029f51687f193bf72eb1ca109e7f2",
    "log4j-api-2.17.1.jar": "b0d8a4c8ab4fb8b1888d0095822703b0e6d4793c419550203da9e69196161de4",
    "spring-beans-5.2.9.RELEASE.jar": "7be619743e6312584deb8c436191cf322d6432131a21c93e723dddf41b3a23fc",
    "spring-expression-5.2.9.RELEASE.jar": "923f9c5c7c06a4e6bd01e0c682a60570c239ee822915899f3b92e53c7dcc524a",
    "spring-jcl-5.2.9.RELEASE.jar": "9d74112b01f62b15d3d4cb330db3af0977782c2422bd956463e20339ec3b0c29",
    "spring-aop-5.2.9.RELEASE.jar": "111f92160ac798d97de09930015286e043655c8a8d1b947a2f2195a76c3514e6",
    "spring-webmvc-5.2.9.RELEASE.jar": "2825194d46c244ff5e64fcba9273bfc8779667a3aac33eb4ea9ef87dfb4fa4ac",
    "javax.servlet-api-4.0.1.jar": "83a03dd877d3674576f0da7b90755c8524af099ccf0607fc61aa971535ad7c60",
    "spring-security-core-5.3.4.RELEASE.jar": "b227c346b807dda45f4144e21df58560a2810af0c03dbbcb73e3119d3269608b",
    "spring-security-crypto-5.3.4.RELEASE.jar": "ec8a6c9138576a1e49071da96dc247b942d1ff84b04524e2cec77cea3cd4ed02",
    "spring-security-config-5.3.4.RELEASE.jar": "9578f6a25e57d3283af328d1e7e862598f49bf45fe15ef2ca53321509b2561c9",
    "spring-security-web-5.3.4.RELEASE.jar": "bac619908d96d79ef20375b430a451d8c782f1bd11295c57bf40e1accdfca273",
    "spring-jdbc-5.2.9.RELEASE.jar": "761ab1d101cf4961e6beae020511b09cfb8273f5a8b69c5ba52ade6209dfe535",
    "spring-tx-5.2.9.RELEASE.jar": "68a088fe9190321225d8bc84d971c1ca72580e84567cca4a806bea07a0eaecd8",
    "ojdbc8-19.3.0.0.jar": "a66d27a14f3adee484427cc4de008af85a5c3e78e2e3285a4dba1277332978a5",
    "cglib-nodep-3.2.5.jar": "9ab68b361ffdce66747f63bbe0676391c0a96d6de3e743ac03d7e998ff0a1064",
    "tomcat-embed-core-9.0.64.jar": "bff331bbe5089f1cdef199591cec58020c4fa5772bd74065c8a717a72e463e72",
    "javax.annotation-api-1.3.2.jar": "e04ba5195bcd555dc95650f7cc614d151e4bcd52d29a10b8aa2197f3ab89ab9b",
    "slf4j-api-1.7.30.jar": "cdba07964d1bb40a0761485c6b1e8c2f8fd9eb1d19c53928ac0d7f9510105c57",
    "thymeleaf-spring5-3.0.11.RELEASE.jar": "c2effd0f4a27419a83bed98f08aab913d00dfa66255768f11821f48867789d73",
    "thymeleaf-3.0.11.RELEASE.jar": "c4decad2647404c3de7bf825e606008d5795738eaa0d12d5d38451de748f1961",
    "attoparser-2.0.5.RELEASE.jar": "d4015d56147f696ed0a90078675bc940529f907e7b2dfc1fad754e8033da8796",
    "unbescape-1.1.6.RELEASE.jar": "597cf87d5b1a4f385b9d1cec974b7b483abb3ee85fc5b3f8b62af8e4bec95c2c",
}
output.mkdir(parents=True, exist_ok=True)
with zipfile.ZipFile(run_jar) as archive:
    for name, expected in dependencies.items():
        content = archive.read("BOOT-INF/lib/" + name)
        if hashlib.sha256(content).hexdigest() != expected:
            raise SystemExit("public dependency hash mismatch: " + name)
        (output / name).write_bytes(content)
