@echo ==================================================
@echo test starting
@echo =============
for %%i in (*.java) do java -cp ../bin Tiger %%i
@echo =============
@echo test finished
@echo ==================================================
