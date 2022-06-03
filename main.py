import base64
from github import Github
from pprint import pprint


def print_repo(repo):
    # repository full name
    print("Full name:", repo.full_name)
    # repository description
    print("Description:", repo.description)
    # the date of when the repo was created
    print("Date created:", repo.created_at)
    # the date of the last git push
    print("Date of last push:", repo.pushed_at)
    # home website (if available)
    print("Home Page:", repo.homepage)
    # programming language
    print("Language:", repo.language)
    # number of forks
    print("Number of forks:", repo.forks)
    # number of stars
    print("Number of stars:", repo.stargazers_count)
    print("-" * 50)
    # repository content (files & directories)
    print("Contents:")
    for content in repo.get_contents(""):
        print(content)
    try:
        # repo license
        print("License:", base64.b64decode(repo.get_license().content.encode()).decode())
    except:
        pass


access_token = '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'
branch_name = 'master'
repo_name = 'NITK-KODE/Use'
g = Github(access_token)
repo = g.get_repo(repo_name)
branch = repo.get_branch(branch_name)
print(branch.commit)

file_path = "main.py"
file = open(file_path, 'r')
file_content = file.read()
file.close()
try:
    contents = repo.get_contents(file_path, ref='test')
    print(contents)
except:
    source_branch = 'master'
    new_branch = 'test'
    sb = repo.get_branch(source_branch)
    repo.create_git_ref(ref='refs/heads/' + new_branch, sha=sb.commit.sha)
    result = repo.create_file(file_path, "added " + file_path, file_content, new_branch)
    print(result)

# def push(path, message, content, new_branch, update=False):
#     source = repo.get_branch("master")
#     repo.create_git_ref(ref=f"refs/heads/{new_branch}", sha=source.commit.sha)
#     if update:  # If file already exists, update it
#         contents = repo.get_contents(path, ref=new_branch)
#         repo.update_file(contents.path, message, content, contents.sha, branch=new_branch)
#     else:
#         repo.create_file(path, message, content, branch=new_branch)
#
#
# push(file_path, "note.txt added", data, "test", update=False)
