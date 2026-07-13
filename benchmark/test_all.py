#!/usr/bin/env python3

import os
import subprocess
import threading
import time
from dataclasses import dataclass
from typing import List

debug: bool = True


class Config:
    java_benchmark_path = "."
    tiger_jar_path = "../build/libs/tiger-1.0.jar"
    tiger_out_path = "../build/"
    compile_info_file = "./compile_info.txt"


@dataclass
class Result:
    exit_code: int
    stdout: str

    def __eq__(self, value) -> bool:
        if not isinstance(value, Result):
            return NotImplemented
        return (self.exit_code == value.exit_code
                and self.stdout == value.stdout)


def print_result(result):
    for k, v in result.items():
        print(k)
        print(v.exit_code)
        print(v.stdout)


# list all .java files
def get_all_java_sources(directory="."):
    java_files = []

    for root, dirs, files in os.walk(directory):
        for cur_file in files:
            if cur_file.endswith('.java'):
                full_path = os.path.join(root, cur_file)
                java_files.append(full_path)
    return java_files


class AtomicCounter:
    def __init__(self, value):
        self._value = value
        self._lock = threading.Lock()

    def increment(self):
        with self._lock:
            self._value += 1

    def get_value(self):
        with self._lock:
            return self._value

    def reset(self):
        with self._lock:
            self.value = 0


total = AtomicCounter(0)
passed = AtomicCounter(0)
num_compile_failures = AtomicCounter(0)
num_run_failures = AtomicCounter(0)
num_result_failures = AtomicCounter(0)
num_oks = AtomicCounter(0)

compile_failed_cases = []
run_failed_cases = []
result_failed_cases = []


@dataclass
class CompileFail(Exception):
    file: str


class RunFail(Exception):
    file: str


class ResultError(Exception):
    file: str


class Ok(Exception):
    file: str


def test_one_case(path, raw_file, expected_result):
    print("testing the file: ", raw_file)

    start = time.time()

    tiger_out_file = os.path.join(raw_file + ".regression.out")
    print("tiger out file: ", tiger_out_file)
    compile_cmd = ["java", "-cp", Config.tiger_jar_path, "Tiger", file, 
                   "-codegen", "x64",
                   "-alloc", "linear",
                   "-trace", "ssa.inline",
                   "-o", tiger_out_file]
    tiger_out_file = os.path.abspath(tiger_out_file)

    if (debug):
        the_str = ""
        for s in compile_cmd:
            the_str = the_str + " " + s
        print(the_str)

    try:
        tiger_compile_result = subprocess.run(compile_cmd,
                                              capture_output=True,
                                              text=True)
    except:
        raise CompileFail(raw_file)

    if tiger_compile_result.returncode != 0:
        print(f"\033[31mTiger compile failed: {tiger_compile_result.returncode}\033[0m")
        raise CompileFail(raw_file)
    else:
        print("tiger compile succeeds")
        with open(Config.compile_info_file, "a+") as f:
            f.write(raw_file+ " out: " + tiger_compile_result.stdout + "\n")

    end = time.time()
    print(f"tiger compile used: {end - start}")

    # tiger run
    start = time.time()
    try:
        tiger_run_result = subprocess.run([tiger_out_file],
                                      capture_output=True,
                                      text=True)
    except:
        print(f"\033[31mTiger run except: {tiger_run_result.returncode}\033[0m")
        raise RunFail(raw_file)

    if tiger_run_result.returncode != 0:
        print(f"\033[31mTiger run wrong exit code: {tiger_run_result.returncode}\033[0m")
        raise RunFail(raw_file)
    else:
        print("tiger run succeeds")
    # all_results.tiger_results[file] = Result(tiger_result.returncode, tiger_result.stdout)
    end = time.time()
    print(f"tiger run used: {end - start}")

    # diff test
    if (expected_result.stdout != tiger_run_result.stdout):
        print(f"\033[31mTiger wrong result: {tiger_run_result.stdout}\033[0m")
        print(f"\033[32mJava        result: {expected_result.stdout}\033[0m")
        raise ResultError(raw_file)
    else:
        raise Ok(raw_file)


def worker(path, file, delay):
    print(f"testing {file} ...")
    global total
    global passed
    global all_failed_cases

    total.increment()
    print(file)
    raw_file = file.removeprefix("./")
    raw_name, ext = os.path.splitext(raw_file)
    print(raw_name, ":", ext)

    # compile with Java (expected output)
    start = time.time()
    try:
        java_result = subprocess.run(["java", raw_file],
                                     capture_output=True, text=True)
    except:
        print("not a valid java file")
        exit(1)
    if java_result.returncode != 0:
        raise ValueError("java expected failed")
    # all_results.expected_results[file] = Result(java_result.returncode, java_result.stdout)
    end = time.time()
    print(f"java used: {end - start}")

    try:
        test_one_case(path, raw_file, java_result)
    except CompileFail as f:
        compile_failed_cases.append(f.args)
        num_compile_failures.increment()
        return
    except RunFail as f:
        run_failed_cases.append(f.args)
        num_run_failures.increment()
        return
    except ResultError as f:
        result_failed_cases.append(f.args)
        num_result_failures.increment()
        return
    except Ok:
        num_oks.increment()
        return

if __name__ == "__main__":
    cur_path = os.path.abspath(os.path.dirname(__file__))

    java_files: List[str] = get_all_java_sources(Config.java_benchmark_path)

    print(f"all files = {len(java_files)}")

    all_threads = []
    for file in java_files:
        the_file = file
        t = threading.Thread(target=worker, args=(cur_path, the_file, 0.1))
        #         all_threads.append(t)
        t.start()
        t.join()

    #     for t in all_threads:
    #         t.start()
    #
    #     for t in all_threads:
    #         t.join()

    total_num = total.get_value()
    num_compile_failures = num_compile_failures.get_value()
    num_run_failures = num_run_failures.get_value()
    num_result_failures = num_result_failures.get_value()
    num_oks = num_oks.get_value()
    print(f"\nSummary:\n"
          f"total: {total_num}\n"
          f"OK: {num_oks}\n"
          f"compile failed: {num_compile_failures}\n"
          f"run failed: {num_run_failures}\n"
          f"result wrong: {num_result_failures}\n"
          )

    print("compile failed:")
    for f in compile_failed_cases:
        print(f)

    print("run failed:")
    for f in run_failed_cases:
    	print(f)

    print("result wrong:")
    for f in result_failed_cases:
    	print(f)
#
#     while not failed_cases.empty():
#         f = failed_cases.get()
#         print(f)
