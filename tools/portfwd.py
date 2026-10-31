#!/usr/bin/env python3
"""极小 TCP 转发器：把宿主端口接到隔离网内容器，供真实页面验收访问。

内部 docker 网络不支持 -p 端口发布，但宿主可直达容器 IP；本进程只做「宿主端口 -> 容器端口」
单向转发，不引入任何出网能力（应用本身仍在无出网路由的 lims-iso 网络内）。

用法: portfwd.py <listen-port> <target-host> <target-port>
"""
import socket
import sys
import threading


def pipe(src: socket.socket, dst: socket.socket) -> None:
    try:
        while True:
            data = src.recv(65536)
            if not data:
                break
            dst.sendall(data)
    except OSError:
        pass
    finally:
        for s in (src, dst):
            try:
                s.shutdown(socket.SHUT_RDWR)
            except OSError:
                pass


def handle(client: socket.socket, target: tuple) -> None:
    try:
        upstream = socket.create_connection(target, timeout=10)
    except OSError:
        client.close()
        return
    threading.Thread(target=pipe, args=(client, upstream), daemon=True).start()
    pipe(upstream, client)


def main() -> None:
    listen_port = int(sys.argv[1])
    target = (sys.argv[2], int(sys.argv[3]))
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server.bind(("127.0.0.1", listen_port))
    server.listen(64)
    sys.stderr.write(f"portfwd 127.0.0.1:{listen_port} -> {target[0]}:{target[1]}\n")
    sys.stderr.flush()
    while True:
        client, _ = server.accept()
        threading.Thread(target=handle, args=(client, target), daemon=True).start()


if __name__ == "__main__":
    main()
